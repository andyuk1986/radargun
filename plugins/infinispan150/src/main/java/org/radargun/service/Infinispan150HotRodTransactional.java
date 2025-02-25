package org.radargun.service;

import jakarta.transaction.TransactionManager;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;

public class Infinispan150HotRodTransactional extends Infinispan93HotRodTransactional {
   protected static final Log log = LogFactory.getLog(Infinispan150HotRodTransactional.class);
   protected static final boolean trace = log.isTraceEnabled();

   public Infinispan150HotRodTransactional(Infinispan150HotrodService service) {
      super(service);
   }

   public Transaction getTransaction() {
      return new Infinispan150HotRodTransactional.Infinispan150Tx();
   }

   protected class Infinispan150Tx extends Infinispan93HotRodTransactional.Tx {
      protected TransactionManager tm;

      @Override
      public <T> T wrap(T resource) {
         if (resource == null) {
            return null;
         }
         TransactionManager tm = getTransactionManager(resource);
         if (this.tm != null && this.tm != tm) {
            throw new IllegalArgumentException("Different transaction managers for single transaction!");
         }
         this.tm = tm;
         // we don't have to wrap anything for Infinispan
         return resource;
      }

      @Override
      public void begin() {
         try {
            tm.begin();
            jakarta.transaction.Transaction transaction = tm.getTransaction();
            if (trace) log.trace("Transaction begin " + transaction);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public void commit() {
         try {
            if (trace) log.trace("Transaction commit " + tm.getTransaction());
            tm.commit();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public void rollback() {
         try {
            if (trace) log.trace("Transaction rollback " + tm.getTransaction());
            tm.rollback();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      private <T> jakarta.transaction.TransactionManager getTransactionManager(T resource) {
         return ((HotRodOperations.HotRodCache) resource).noReturn.getTransactionManager();
      }
   }
}

package org.radargun.service;

import jakarta.transaction.TransactionManager;

public class Infinispan150Transactional extends Infinispan51Transactional {

   public Infinispan150Transactional(Infinispan51EmbeddedService service) {
      super(service);
   }

   @Override
   public Transaction getTransaction() {
      return new Infinispan150Tx();
   }

   protected class Infinispan150Tx extends InfinispanTransactional.Tx {
      protected TransactionManager tm;
      @Override
      public <T> T wrap(T resource) {
         if (resource == null) {
            return null;
         }
         TransactionManager tm = getAdvancedCache(resource).getTransactionManager();
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
            if (enlistExtraXAResource) {
               transaction.enlistResource(new DummyXAResource());
            }
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
   }
}

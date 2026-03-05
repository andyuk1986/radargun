package org.radargun.service;

import org.radargun.traits.ContinuousQuery;
import org.radargun.traits.Query;

public class Infinispan160EmbeddedContinuousQuery implements ContinuousQuery {
   protected final Infinispan160FailureEmbeddedService service;

   public Infinispan160EmbeddedContinuousQuery(Infinispan160FailureEmbeddedService service) {
      this.service = service;
   }

   @Override
   public ListenerReference createContinuousQuery(String cacheName, Query query, ContinuousQuery.Listener cqListener) {
      AbstractInfinispanQueryable.QueryImpl ispnQuery = (AbstractInfinispanQueryable.QueryImpl) query;
      org.infinispan.commons.api.query.ContinuousQuery cq =   service.getCache(cacheName).continuousQuery();
      Infinispan160EmbeddedContinuousQuery.Listener ispnCqListener = new Infinispan160EmbeddedContinuousQuery.Listener(cqListener);
      cq.addContinuousQueryListener(ispnQuery.getDelegatingQuery(), ispnCqListener);
      return new Infinispan160EmbeddedContinuousQuery.ListenerReference(cq, ispnCqListener);
   }

   @Override
   public void removeContinuousQuery(String cacheName, ContinuousQuery.ListenerReference listenerReference) {
      Infinispan160EmbeddedContinuousQuery.ListenerReference ref = (Infinispan160EmbeddedContinuousQuery.ListenerReference) listenerReference;
      ref.cq.removeContinuousQueryListener(ref.listener);
   }

   private static class Listener implements org.infinispan.commons.api.query.ContinuousQueryListener {

      private final ContinuousQuery.Listener cqListener;

      public Listener(ContinuousQuery.Listener cqListener) {
         this.cqListener = cqListener;
      }

      @Override
      public void resultJoining(Object key, Object value) {
         cqListener.onEntryJoined(key, value);
      }

      @Override
      public void resultLeaving(Object key) {
         cqListener.onEntryLeft(key);
      }
   }

   public static class ListenerReference implements ContinuousQuery.ListenerReference {
      private final org.infinispan.commons.api.query.ContinuousQuery<Object, Object> cq;
      private final Infinispan160EmbeddedContinuousQuery.Listener listener;

      public ListenerReference(org.infinispan.commons.api.query.ContinuousQuery<Object, Object> cq, Infinispan160EmbeddedContinuousQuery.Listener listener) {
         this.cq = cq;
         this.listener = listener;
      }
   }

}

package org.radargun.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.event.ClientEvents;
import org.radargun.traits.ContinuousQuery;
import org.radargun.traits.Query;

/**
 * @author Anna Manukyan &lt;amanukya@redhat.com&gt;
 */
public class Infinispan160HotrodContinuousQuery implements ContinuousQuery {

   protected final InfinispanHotrodService service;

   public Infinispan160HotrodContinuousQuery(InfinispanHotrodService service) {
      this.service = service;
   }

   @Override
   public Infinispan160HotrodContinuousQuery.ListenerReference createContinuousQuery(String cacheName, Query query, ContinuousQuery.Listener cqListener) {
      AbstractInfinispanQueryable.QueryImpl ispnQuery = (AbstractInfinispanQueryable.QueryImpl) query;
      Infinispan160HotrodContinuousQuery.Listener ispnCqListener = new Infinispan160HotrodContinuousQuery.Listener(cqListener);
      ClientEvents.addClientQueryListener(getRemoteCache(cacheName), ispnCqListener, ispnQuery.getDelegatingQuery());

      getRemoteCache(cacheName).continuousQuery().addContinuousQueryListener(ispnQuery.getDelegatingQuery(), ispnCqListener);
      return new Infinispan160HotrodContinuousQuery.ListenerReference(ispnCqListener);
   }

   @Override
   public void removeContinuousQuery(String cacheName, ContinuousQuery.ListenerReference listenerReference) {
      Infinispan160HotrodContinuousQuery.ListenerReference ref = (Infinispan160HotrodContinuousQuery.ListenerReference) listenerReference;
      getRemoteCache(cacheName).removeClientListener(ref.clientListener);
   }

   protected RemoteCache getRemoteCache(String cacheName) {
      return cacheName == null ? service.managerNoReturn.getCache() : service.managerNoReturn.getCache(cacheName);
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

   private static class ListenerReference implements ContinuousQuery.ListenerReference {
      private final Object clientListener;

      private ListenerReference(Object clientListener) {
         this.clientListener = clientListener;
      }
   }
}

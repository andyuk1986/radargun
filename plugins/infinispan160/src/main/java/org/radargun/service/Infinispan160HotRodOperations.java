package org.radargun.service;

import org.infinispan.client.hotrod.RemoteCache;

public class Infinispan160HotRodOperations extends Infinispan90HotRodOperations {

   public Infinispan160HotRodOperations(InfinispanHotrodService service) {
      super(service);
   }

   @Override
   public <K, V> HotRodCache<K, V> getCache(String cacheName) {
      if (cacheName == null) {
         cacheName = service.cacheName;
      }
      return new HotRodCache<>((RemoteCache<K, V>) service.managerNoReturn.getCache(cacheName),
         (RemoteCache<K, V>) service.managerForceReturn.getCache(cacheName));
   }
}

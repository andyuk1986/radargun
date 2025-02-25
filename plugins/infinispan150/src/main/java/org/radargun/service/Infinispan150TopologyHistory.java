package org.radargun.service;

import org.infinispan.Cache;
import org.infinispan.distribution.ch.ConsistentHash;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.event.TopologyChangedEvent;

public class Infinispan150TopologyHistory extends Infinispan70TopologyHistory {

   public Infinispan150TopologyHistory(Infinispan60EmbeddedService service) {
      super(service);
   }

   @Override
   public void registerListener(Cache<?, ?> cache) {
      cache.addListener(new Infinispan150TopologyAwareListener(cache.getName()));
   }

   @Listener
   public class Infinispan150TopologyAwareListener extends Infinispan70TopologyAwareListener {

      public Infinispan150TopologyAwareListener(String cacheName) {
         super(cacheName);
      }

      protected ConsistentHash getConsistentHashAtStart(TopologyChangedEvent<?, ?> e) {
         return e.getReadConsistentHashAtStart();
      }

      protected ConsistentHash getConsistentHashAtEnd(TopologyChangedEvent<?, ?> e) {
         return e.getWriteConsistentHashAtEnd();
      }
   }
}

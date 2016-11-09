package org.radargun.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;
import org.radargun.traits.CacheInformation;

/**
 * CacheInfo class for NodeJs Plugin.
 *
 * @author Anna Manukyan
 */
public class NodeJsCacheInfo implements CacheInformation {
   protected final NodeJsClientService service;
   private static final Log log = LogFactory.getLog(NodeJsCacheInfo.class);

   public NodeJsCacheInfo(NodeJsClientService service) {
      this.service = service;
   }

   @Override
   public String getDefaultCacheName() {
      return service.getCacheName();
   }

   @Override
   public Collection<String> getCacheNames() {
      return Arrays.asList(service.getCacheName());
   }

   @Override
   public Cache getCache(String cacheName) {
      return new Cache(service.getClientEndPoint());
   }

   protected class Cache implements CacheInformation.Cache {
      protected NodeJsHttpClient client;

      public Cache(NodeJsHttpClient client) {
         this.client = client;
      }

      @Override
      public long getOwnedSize() {
         return -1;
      }

      @Override
      public long getLocallyStoredSize() {
         return -1;
      }

      @Override
      public long getMemoryStoredSize() {
         return -1;
      }

      @Override
      public long getTotalSize() {
         long cacheSize = 0;

         try {
            cacheSize = Integer.parseInt(client.send("/size", null));
         } catch (Exception e) {
            log.error("Exception appeared while retrieving the cache size. " + e.getMessage());
         }

         return cacheSize;
      }

      @Override
      public Map<?, Long> getStructuredSize() {
         return new HashMap<>();
      }

      @Override
      public int getNumReplicas() {
         return -1;
      }

      @Override
      public int getEntryOverhead() {
         return -1;
      }
   }
}

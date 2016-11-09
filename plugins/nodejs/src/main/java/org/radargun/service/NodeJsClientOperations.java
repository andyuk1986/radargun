package org.radargun.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;
import org.radargun.traits.BasicOperations;

/**
 * Client operations which connect to NodeJS Http Client and perform the corresponding operation.
 *
 * @author Anna Manukyan
 */
public class NodeJsClientOperations implements BasicOperations {
   private static final Log log = LogFactory.getLog(NodeJsClientOperations.class);
   private final NodeJsClientService service;

   public NodeJsClientOperations(NodeJsClientService service) {
      this.service = service;
   }

   @Override
   public <K, V> Cache<K, V> getCache(String cacheName) {
      if (service.isRunning()) {
         if (cacheName != null && (service.cacheName == null || !service.cacheName.equals(cacheName))) {
            throw new UnsupportedOperationException();
         }
         return new NodeJsCache<K, V>();
      }
      return null;
   }

   protected class NodeJsCache<K, V> implements BasicOperations.Cache<K, V> {

      @Override
      public V get(K key) {
         V value = null;
         if (service.isRunning()) {
            try {
               Map<String, String> params = new HashMap<String, String>();
               params.put("key", (String) key);
               value = valueConverter(service.getClientEndPoint().send("/get", params));
            } catch (IOException e) {
               log.error("Exception appeared while get operation!");
               throw new RuntimeException(e);
            } catch (Exception ex) {
               ex.printStackTrace();
            }
         }

         return value;
      }

      @Override
      public boolean containsKey(K key) {
         boolean contains = false;
         if (service.isRunning()) {
            try {
               Map<String, String> params = new HashMap<String, String>();
               params.put("key", (String) key);
               contains = Boolean.parseBoolean(service.getClientEndPoint().send("/containsKey", params));
            } catch (IOException e) {
               log.error("Exception appeared while containsKey operation!");
               throw new RuntimeException(e);
            }
         }
         return contains;
      }

      @Override
      public void put(K key, V value) {
         if (service.isRunning()) {
            try {
               Map<String, String> params = new HashMap<String, String>();
               params.put("key", (String) key);
               params.put("value", (String) value);

               log.info("Sending put request for " + key);
               service.getClientEndPoint().send("/put", params);
               log.info("Received response for " + key);
            } catch (IOException e) {
               log.error("Exception appeared while put operation!");
               throw new RuntimeException(e);
            }
         }
      }

      @Override
      public V getAndPut(K key, V value) {
         V prevValue = null;
         if (service.isRunning()) {
            try {
               Map<String, String> params = new HashMap<String, String>();
               params.put("key", (String) key);
               params.put("value", (String) value);
               prevValue = valueConverter(service.getClientEndPoint().send("/getAndPut", params));
            } catch (IOException e) {
               log.error("Exception appeared while getAndPut operation!");
               throw new RuntimeException(e);
            }
         }

         return prevValue;
      }

      @Override
      public boolean remove(K key) {
         boolean returnValue = false;
         if (service.isRunning()) {
            try {
               Map<String, String> params = new HashMap<String, String>();
               params.put("key", (String) key);
               returnValue = Boolean.parseBoolean(service.getClientEndPoint().send("/remove", params));
            } catch (IOException e) {
               log.error("Exception appeared while remove operation!");
               throw new RuntimeException(e);
            }
         }
         return returnValue;
      }

      @Override
      public V getAndRemove(K key) {
         V value = null;
         if (service.isRunning()) {
            try {
               Map<String, String> params = new HashMap<String, String>();
               params.put("key", (String) key);
               value = valueConverter(service.getClientEndPoint().send("/getAndRemove", params));
            } catch (IOException e) {
               log.error("Exception appeared while getAndRemove operation!");
               throw new RuntimeException(e);
            }
         }
         return value;
      }

      @Override
      public void clear() {
         if (service.isRunning()) {
            try {
               service.getClientEndPoint().send("/clear", null);
            } catch (IOException e) {
               log.error("Exception appeared while clear operation!");
               throw new RuntimeException(e);
            }
         }
      }

      private V valueConverter(String value) {
         return (V) value;
      }
   }
}

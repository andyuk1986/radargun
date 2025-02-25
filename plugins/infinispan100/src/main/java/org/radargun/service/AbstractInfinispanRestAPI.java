package org.radargun.service;

import java.util.concurrent.ExecutionException;

/**
 * Abstract InfinispanRestAPI with corresponding methods to use in further plugin implementations.
 */
public abstract class AbstractInfinispanRestAPI {

   public abstract CacheManagerInfo getCacheManager() throws RestException;

   public abstract void stopCluster(long stopTimeout);

   public abstract void info() throws ExecutionException, InterruptedException;

   static class RestException extends Exception {
      public RestException(String message, Exception e) {
         super(message, e);
      }
   }
}

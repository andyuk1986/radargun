package org.radargun.service;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.radargun.Service;
import org.radargun.config.Init;
import org.radargun.traits.ContinuousQuery;
import org.radargun.traits.ProvidesTrait;

@Service(doc = Infinispan60HotrodService.SERVICE_DESCRIPTION)
public class Infinispan160HotrodService extends Infinispan150HotrodService {
   protected Configuration configurationForceReturn;

   @Init
   public void init() {
      super.init();

      ConfigurationBuilder builder = getDefaultConfigurationBuilder();
      builder.forceReturnValues(true);
      configurationForceReturn = builder.build();
   }

   @Override
   public void start() {
      managerNoReturn = new RemoteCacheManager(configuration, true);
      managerForceReturn = new RemoteCacheManager(configurationForceReturn, true);
      if (queryable != null) {
         queryable.registerProtofilesRemote();
      }
   }

   @Override
   protected ConfigurationBuilder setNearCachingConfig(ConfigurationBuilder cb) {
      cb.remoteCache("").nearCacheMode(nearCachingConfig.mode)
         .nearCacheMaxEntries(nearCachingConfig.maxEntries);

      return cb;
   }

   @ProvidesTrait
   public ContinuousQuery createContinuousQuery() {
      return new Infinispan160HotrodContinuousQuery(this);
   }

   @ProvidesTrait
   public HotRodOperations createOperations() {
      return new Infinispan160HotRodOperations(this);
   }

   @ProvidesTrait
   public Infinispan160HotRodCacheInfo creeateCacheInfo() {
      return new Infinispan160HotRodCacheInfo(this);
   }
}

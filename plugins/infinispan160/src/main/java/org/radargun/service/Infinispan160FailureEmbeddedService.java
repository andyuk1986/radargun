package org.radargun.service;

import java.io.IOException;

import org.infinispan.configuration.parsing.ConfigurationBuilderHolder;
import org.infinispan.distribution.DistributionManager;
import org.infinispan.distribution.ch.ConsistentHash;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.radargun.Service;
import org.radargun.traits.ContinuousQuery;
import org.radargun.traits.ProvidesTrait;

@Service(doc = InfinispanEmbeddedService.SERVICE_DESCRIPTION)
public class Infinispan160FailureEmbeddedService extends Infinispan150FailureEmbeddedService {
   protected Infinispan160Lifecycle createLifecycle() {
      return new Infinispan160Lifecycle(this);
   }

   @ProvidesTrait
   @Override
   public ContinuousQuery createContinuousQuery() {
      return new Infinispan160EmbeddedContinuousQuery(this);
   }

   protected InfinispanClustered createClustered() {
      return new Infinispan160Clustered(this);
   }

   protected DefaultCacheManager createCacheManager(String configFile) throws IOException {
      ConfigurationBuilderHolder cbh = createConfiguration(configFile);
      cbh.getGlobalConfigurationBuilder().transport().transport((JGroupsTransport) partitionable.createTransport());
      DefaultCacheManager cm = new DefaultCacheManager(cbh, false);
      beforeCacheManagerStart(cm);
      return cm;
   }

   protected ConsistentHash getReadConsistentHash(DistributionManager dm) {
      return dm.getCacheTopology().getReadConsistentHash();
   }

   protected ConsistentHash getWriteConsistentHash(DistributionManager dm) {
      return dm.getCacheTopology().getWriteConsistentHash();
   }

}

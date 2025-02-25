package org.radargun.service;

import org.infinispan.remoting.transport.Transport;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.infinispan.security.actions.SecurityActions;
import org.jgroups.protocols.TP;
import org.radargun.Service;
import org.radargun.traits.ProvidesTrait;

@Service(doc = InfinispanEmbeddedService.SERVICE_DESCRIPTION)
public class Infinispan150FailureEmbeddedService extends Infinispan121FailureEmbeddedService {
   protected Infinispan150Lifecycle createLifecycle() {
      return new Infinispan150Lifecycle(this);
   }

   protected JGroupsTransport getTransport() {
      return (JGroupsTransport) SecurityActions.getGlobalComponentRegistry(cacheManager).getComponent(Transport.class);
   }

   protected TP getTransportProtocol() {
      JGroupsTransport transport = getTransport();
      return (TP) transport.getChannel().getProtocolStack().findProtocol(TP.class);
   }

   @ProvidesTrait
   public Infinispan150TopologyHistory getInfinispan150TopologyHistory() {
      return (Infinispan150TopologyHistory) topologyAware;
   }

   @Override
   protected InfinispanTopologyHistory createTopologyAware() {
      return new Infinispan150TopologyHistory(this);
   }

   @Override
   public InfinispanTransactional createTransactional() {
      return new Infinispan150Transactional(this);
   }
}

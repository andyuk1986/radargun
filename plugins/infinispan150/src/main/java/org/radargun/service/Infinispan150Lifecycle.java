package org.radargun.service;

import org.infinispan.remoting.transport.Transport;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.infinispan.security.actions.SecurityActions;

public class Infinispan150Lifecycle extends Infinispan100Lifecycle {

   public Infinispan150Lifecycle(Infinispan150FailureEmbeddedService service) {
      super(service);
   }

   @Override
   protected JGroupsTransport getTransport() {
      return (JGroupsTransport) SecurityActions.getGlobalComponentRegistry(service.cacheManager).getComponent(Transport.class);
   }
}

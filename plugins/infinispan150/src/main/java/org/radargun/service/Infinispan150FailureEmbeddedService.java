package org.radargun.service;

import org.radargun.Service;

@Service(doc = InfinispanEmbeddedService.SERVICE_DESCRIPTION)
public class Infinispan150FailureEmbeddedService extends Infinispan121FailureEmbeddedService {
   protected Infinispan150Lifecycle createLifecycle() {
      return new Infinispan150Lifecycle(this);
   }
}

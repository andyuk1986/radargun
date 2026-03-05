package org.radargun.service;

public class Infinispan160Lifecycle extends Infinispan150Lifecycle {

   public Infinispan160Lifecycle(Infinispan160FailureEmbeddedService service) {
      super(service);
   }

   protected void printInfinispanVersion() {
      log.info("Infinispan version: " + org.infinispan.commons.util.Version.printVersion());
   }
}

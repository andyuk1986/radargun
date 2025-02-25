package org.radargun.service;

import java.util.concurrent.TimeUnit;

import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.radargun.Service;
import org.radargun.traits.ProvidesTrait;

/**
 * @author Roman Macor (rmacor@redhat.com)
 */
@Service(doc = InfinispanEmbeddedService.SERVICE_DESCRIPTION)
public class Infinispan90EmbeddedService extends Infinispan82EmbeddedService {

   @Override
   protected Infinispan90Lifecycle createLifecycle() {
      return new Infinispan90Lifecycle(this);
   }

   @Override
   @ProvidesTrait
   public InfinispanCacheInfo createCacheInformation() {
      return new Infinispan90CacheInfo(this);
   }

   @Override
   @ProvidesTrait
   public InfinispanEmbeddedQueryable createQueryable() {
      return new Infinispan90EmbeddedQueryable(this);
   }

   @Override
   protected void startJGroupsDumper(Runnable thread) {
      JGroupsTransport transport = getTransport();
      if (transport == null || transport.getChannel() == null || !transport.getChannel().isOpen()) {
         // JGroups are not initialized, wait
         scheduledExecutor.schedule(thread, 1, TimeUnit.SECONDS);
      } else {
         jgroupsDumper = new JGroups4Dumper(transport.getChannel().getProtocolStack(), jgroupsDumperInterval);
         jgroupsDumper.start();
      }
   }
}
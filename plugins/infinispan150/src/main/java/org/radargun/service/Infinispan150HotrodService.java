package org.radargun.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.radargun.Service;
import org.radargun.traits.ProvidesTrait;
import org.radargun.traits.Transactional;

@Service(doc = Infinispan60HotrodService.SERVICE_DESCRIPTION)
public class Infinispan150HotrodService extends Infinispan120HotrodService {

   @ProvidesTrait
   public Transactional createTransactional() {
      return new Infinispan150HotRodTransactional(this);
   }

   public boolean isCacheTransactional(RemoteCache remoteCache) {
      return remoteCache.isTransactional();
   }

}

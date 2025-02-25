package org.radargun.service;

import java.io.IOException;

import org.radargun.Service;


@Service(doc = InfinispanServerService.SERVICE_DESCRIPTION)
public class Infinispan150ServerService extends Infinispan110ServerService {
   protected void initServerClustered() throws IOException {
      clustered = new Infinispan150ServerClustered(this, defaultServerPort, username, password);
   }
}

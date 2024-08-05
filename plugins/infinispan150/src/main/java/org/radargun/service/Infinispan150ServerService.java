package org.radargun.service;

import java.io.IOException;

public class Infinispan150ServerService extends Infinispan110ServerService {
   protected void initServerClustered() throws IOException {
      clustered = new Infinispan150ServerClustered(this, defaultServerPort, username, password);
   }
}

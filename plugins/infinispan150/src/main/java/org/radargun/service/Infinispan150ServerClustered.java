package org.radargun.service;

import java.io.IOException;

/**
 * @author Anna Manukyan &lt;amanukya@redhat.com&gt;
 */
public class Infinispan150ServerClustered extends Infinispan100ServerClustered {

   public Infinispan150ServerClustered(Infinispan150ServerService service, Integer defaultPort, String username, String password) throws IOException {
      super(service);
      setRestAPI(new Infinispan150RestAPI(defaultPort, username, password));
   }
}

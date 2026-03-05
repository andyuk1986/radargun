package org.radargun.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.infinispan.notifications.Listener;
import org.infinispan.remoting.transport.Address;

@Listener
public class Infinispan160Clustered extends InfinispanClustered{

   public Infinispan160Clustered(Infinispan160FailureEmbeddedService service) {
      super(service);
   }

   protected Collection<Member> convert(List<Address> addresses) {
      Collection<Member> members = new ArrayList<>(addresses.size());
      boolean coord = true;
      for (Address address : addresses) {
         members.add(new Member(address.toString(), service.cacheManager.getAddress().equals(address), coord));
         coord = false;
      }
      return members;
   }
}

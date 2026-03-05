package org.radargun.service;


/**
 * @author Anna Manukyan &lt;amanukya@redhat.com&gt;
 */
public class Infinispan160HotRodCacheInfo extends Infinispan60HotRodCacheInfo {

   private Infinispan160HotrodService service;

   public Infinispan160HotRodCacheInfo(Infinispan160HotrodService service) {
      super(service);
      this.service = service;
   }

   @Override
   public Cache getCache(String cacheName) {
      return new Cache(service.managerForceReturn.getCache(cacheName));
   }
}

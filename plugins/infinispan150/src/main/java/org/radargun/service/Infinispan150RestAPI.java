package org.radargun.service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.infinispan.client.rest.RestResponse;
import org.infinispan.client.rest.configuration.RestClientConfigurationBuilder;
import org.infinispan.client.rest.impl.jdk.RestClientJDK;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;

/**
 * <p> RestClient to consume Infinispan REST API to interact with the Cache Manager and obtain cluster and usage statistics. </p>
 * <p> @see <a href="https://github.com/infinispan/infinispan/blob/main/documentation/src/main/asciidoc/topics/rest_api_v2.adoc#cache-manager">Infinispan REST API</a> </p>
 *
 * @author Anna Manukyan &lt;amanukya@redhat.com&gt;
 */
public class Infinispan150RestAPI extends AbstractInfinispanRestAPI {

   // ms
   private static final int DEFAULT_TIMEOUT = 5_000;

   protected final Log log = LogFactory.getLog(getClass());

   private final ObjectMapper mapper;
   private final RestClientJDK restClientJDK;
   private final String cacheManagerName;

   public Infinispan150RestAPI(Integer serverPort, String username, String password) throws IOException {
      this.mapper = new ObjectMapper();
      this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      this.mapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());
      this.cacheManagerName = System.getProperty("radargun.infinispan.cacheManagerName", "clustered");
      RestClientConfigurationBuilder config = new RestClientConfigurationBuilder();
      config.socketTimeout(DEFAULT_TIMEOUT)
         .connectionTimeout(DEFAULT_TIMEOUT)
         .addServer()
         .host(lookupServerHost())
         .port(serverPort).pingOnCreate(false);
      if (username != null && password != null) {
         config.security().authentication().enable().username(username).password(password);
      }

      this.restClientJDK = new RestClientJDK(config.build());
   }

   private String lookupServerHost() throws IOException {
      String infinispanHost = System.getProperty("infinispan.bind.address", "127.0.0.1");
      log.info(String.format("Infinispan API is going to connect to: %s", infinispanHost));
      return infinispanHost;
   }

   public CacheManagerInfo getCacheManager() throws RestException {
      CacheManagerInfo cacheManagerInfo;
      CompletableFuture<RestResponse> responseFuture = restClientJDK.container().info().toCompletableFuture();
      RestResponse response;
      try {
         response = responseFuture.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
         String body = response.body();
         try {
            cacheManagerInfo = mapper.readValue(body, CacheManagerInfo.class);
         } catch (JsonProcessingException e) {
            log.error("Http status: " + response.status() + ", Http body: " + body);
            throw new RestException("Cannot parse the response", e);
         }
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         throw new RestException("Cannot retrieve the response", e);
      }
      return cacheManagerInfo;
   }

   public void stopCluster(long stopTimeout) {
      try {
         restClientJDK.cluster().stop().toCompletableFuture().get(stopTimeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         // the stage should be responsible to check if the node still alive
      }
   }

   public void info() throws ExecutionException, InterruptedException {
      restClientJDK.server().info().toCompletableFuture().get();
   }
}

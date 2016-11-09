package org.radargun.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.radargun.Service;
import org.radargun.config.Property;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;
import org.radargun.traits.Lifecycle;
import org.radargun.traits.ProvidesTrait;

/**
 * NodeJS Client service which install and starts the node project by given path.
 * Also the service initializes the httpClient which performs requests to NodeJs Http Server.
 *
 * @author Anna Manukyan
 */
@Service(doc = "NodeJS client")
public class NodeJsClientService implements Lifecycle {

   private static final Log log = LogFactory.getLog(NodeJsClientService.class);
   private Process process;
   private NodeJsHttpClient clientEndPoint;

   @Property(doc = "The username to use on an authenticated server. Defaults to null.")
   private String username;

   @Property(doc = "The password of the username to use on an authenticated server. Defaults to null.")
   private String password;

   @Property(doc = "Expected cache name. Requests for other caches will fail. Defaults to 'default'.")
   protected String cacheName = "default";

   @Property(doc = "List of server addresses the clients should connect to, separated by semicolons (;).")
   protected String servers;

   @Property(doc = "The path to nodeJs project.")
   protected String pathToNodeProject;

   @Property(doc = "The path to nodejs script.")
   protected String pathToScript;

   @Property(doc = "The path to infinispan nodeJs client. The default is infinispan and will be installed from the npmjs.org repository.")
   protected String pathToInfinispanNodeJsClient = "infinispan";

   @Property(doc = "The url of started nodejs server.")
   protected String urlToNodeJsServer;

   @ProvidesTrait
   public Lifecycle getLifecycle() {
      return this;
   }

   @ProvidesTrait
   public NodeJsClientOperations createOperations() {
      return new NodeJsClientOperations(this);
   }

   @ProvidesTrait
   public NodeJsCacheInfo createCacheInformation() {
      return new NodeJsCacheInfo(this);
   }

   @Override
   public synchronized void start() {
      log.info("Starting NodeJS service!");
      BufferedReader bufferedReader = null;
      try {
         ProcessBuilder processBuilder = new ProcessBuilder("npm", "install", pathToInfinispanNodeJsClient);
         processBuilder.directory(new File(pathToNodeProject));
         process = processBuilder.start();
         bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1);
         String line;
         while ((line = bufferedReader.readLine()) != null) {
            log.info("NPM Install OUTPUT: " + line);
         }

         int exitValue = process.waitFor();
         if (exitValue == 0) { //Checking if the npm install completed properly then continuing with the script run.
            log.info("NPM installing is successfully done! Heading up to node script running.");

            processBuilder = new ProcessBuilder("node", pathToScript, servers);
            processBuilder.directory(new File(pathToNodeProject));
            process = processBuilder.start();
            ReaderThread readerThread = new ReaderThread(process.getInputStream());
            readerThread.start();

            Thread.sleep(2000);

            clientEndPoint = new NodeJsHttpClient(urlToNodeJsServer);
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (bufferedReader != null) {
            try {
               bufferedReader.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public synchronized void stop() {
      if (process == null && clientEndPoint == null) {
         log.warn("Service not started");
         return;
      }
      log.info("Disconnecting from socket.....");
      process.destroy();

      process = null;
      clientEndPoint = null;
   }

   @Override
   public synchronized boolean isRunning() {
      return process != null && process.isAlive();
   }

   public NodeJsHttpClient getClientEndPoint() {
      return clientEndPoint;
   }

   public String getCacheName() {
      return cacheName;
   }

   class ReaderThread extends Thread {
      private InputStream inputStream;

      ReaderThread(InputStream inputStream) {
         this.inputStream = inputStream;
      }

      public void run() {
         String line;

         try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1)) {
            while ((line = bufferedReader.readLine()) != null) {
               log.info(line);
            }
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}

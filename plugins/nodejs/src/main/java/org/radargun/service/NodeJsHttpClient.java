package org.radargun.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.radargun.logging.Log;
import org.radargun.logging.LogFactory;

/**
 * An HttpClient which sends requests to NodeJs Http Server.
 * The NodeJs Http Server distinguishes based on the path which operation should be executed and returns the response
 * as soon as the operation is done.
 *
 * @author Anna Manukyan
 */
public class NodeJsHttpClient {
   private static final Log log = LogFactory.getLog(NodeJsHttpClient.class);
   private HttpClient client;
   private String url;
   private int timeout = 10 * 1000;

   public NodeJsHttpClient(String url) {
      this.url = url;
      RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout)
         .setConnectTimeout(timeout)
         .setConnectionRequestTimeout(timeout).build();
      client = HttpClientBuilder.create()
         .setDefaultRequestConfig(requestConfig)
         .setMaxConnPerRoute(20000)
         .setMaxConnTotal(30000)
         .setConnectionTimeToLive(10, TimeUnit.SECONDS)
         .build();
   }

   public String send(String path, Map<String, String> params) throws IOException {
      String responseStr = null;
      HttpGet request = new HttpGet(prepareUrl(path, params));

      HttpResponse response = client.execute(request);
      if (response.getStatusLine().getStatusCode() == 200) {
         BufferedReader rd = new BufferedReader(
            new InputStreamReader(response.getEntity().getContent()));
         responseStr = rd.readLine();
      }

      response.getEntity().getContent().close();
      return responseStr;
   }

   private String prepareUrl(String path, Map<String, String> params) {
      StringBuffer str = new StringBuffer(url).append(path).append("?");

      if (params != null) {
         for (Map.Entry<String, String> param : params.entrySet()) {
            str.append(param.getKey()).append("=").append(param.getValue()).append("&");
         }
      }

      return str.toString();
   }
}

package org.radargun;

import java.util.List;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.radargun.stages.test.Invocation;
import org.radargun.traits.RESTOperations;

/**
 * Provides {@link Invocation} implementations for operations from traits
 * {@link RESTOperationInvocations}
 *
 * @author Martin Gencur
 */
public class RESTOperationInvocations {
   public static final class Get implements Invocation<Response> {
      private final RESTOperations.RESTOperationInvoker httpInvoker;
      private List<Cookie> cookies;
      private MultivaluedMap<String, Object> headers;

      public Get(RESTOperations.RESTOperationInvoker httpInvoker, List<Cookie> cookies, MultivaluedMap<String, Object> headers) {
         this.httpInvoker = httpInvoker;
         this.cookies = cookies;
         this.headers = headers;
      }

      @Override
      public Response invoke() {
         return httpInvoker.get(cookies, headers);
      }

      @Override
      public Operation operation() {
         return RESTOperations.GET;
      }

      @Override
      public Operation txOperation() {
         return RESTOperations.GET;
      }
   }
}

 package ext.mocker.common.httpserver;

 import io.netty.handler.codec.http.FullHttpRequest;

 import java.util.Map;

 public class HttpMockRequest
 {
   private FullHttpRequest request;
   private Map<String, String> requestParams;
   private Map<String, String> responseHeaders;

   private Object requestContent;

   public FullHttpRequest getRequest()
   {
     return this.request;
   }

   public void setRequest(FullHttpRequest request)
   {
     this.request = request;
   }

   public Map<String, String> getRequestParams()
   {
     return this.requestParams;
   }

   public void setRequestParams(Map<String, String> requestParams)
   {
     this.requestParams = requestParams;
   }

   public Object getRequestContent()
   {
     return this.requestContent;
   }

   public void setRequestContent(Object requestContent)
   {
     this.requestContent = requestContent;
   }

   public Map<String, String> getResponseHeaders()
   {
     return this.responseHeaders;
   }

   public void setResponseHeaders(Map<String, String> responseHeaders)
   {
     this.responseHeaders = responseHeaders;
   }





 }



package com.auto.ext.mocker.common.httpserver;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectDecoder;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.util.concurrent.FastThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpAdaptiveDecoder
  extends HttpObjectDecoder
{
  static Logger logger = LoggerFactory.getLogger(HttpAdaptiveDecoder.class);
  private static FastThreadLocal<Boolean> isDecodingRequestThreadLocal = new FastThreadLocal()
  {
    protected Boolean initialValue()
      throws Exception
    {
      return Boolean.FALSE;
    }
  };
  private HttpAdaptiveRequestDecoder requestDecoder = new HttpAdaptiveRequestDecoder( );
  private HttpAdaptiveResponseDecoder responseDecoder = new HttpAdaptiveResponseDecoder( );

  protected boolean isDecodingRequest()
  {
    Boolean isDecodingResponse = (Boolean)isDecodingRequestThreadLocal.get();
    return !isDecodingResponse.booleanValue();
  }

  protected HttpMessage createMessage(String[] initialLine)
    throws Exception
  {
    boolean isDecodingResponse = StringUtils.startsWith(initialLine[0], "HTTP");
    isDecodingRequestThreadLocal.set(Boolean.valueOf(isDecodingResponse));
    return isDecodingResponse ? this.responseDecoder.createMessage(initialLine) : this.requestDecoder.createMessage(initialLine);
  }

  protected HttpMessage createInvalidMessage()
  {
    return isDecodingRequest() ? this.requestDecoder.createInvalidMessage() : this.responseDecoder.createInvalidMessage();
  }

  private static class HttpAdaptiveRequestDecoder
    extends HttpRequestDecoder
  {
    public HttpMessage createMessage(String[] initialLine)
      throws Exception
    {
      return super.createMessage(initialLine);
    }

    public HttpMessage createInvalidMessage()
    {
      return super.createInvalidMessage();
    }
  }

  private static class HttpAdaptiveResponseDecoder
    extends HttpResponseDecoder
  {
    public HttpMessage createMessage(String[] initialLine)
    {
      return super.createMessage(initialLine);
    }

    public HttpMessage createInvalidMessage()
    {
      return super.createInvalidMessage();
    }
  }
}




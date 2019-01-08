  package com.auto.ext.mocker.common.pool;

  import com.auto.ext.mocker.common.util.HttpHelper;
  import io.netty.channel.ChannelHandlerContext;
  import io.netty.channel.SimpleChannelInboundHandler;
  import io.netty.handler.codec.http.FullHttpResponse;
  import org.apache.commons.lang3.StringUtils;

  import java.util.concurrent.CountDownLatch;
  import java.util.concurrent.TimeUnit;

  public class SimpleChannelInboundResponseHandler
    extends SimpleChannelInboundHandler<FullHttpResponse>
  {
    private String responseContent;
    private String transactionId;
    private CountDownLatch operationCompleted = new CountDownLatch(1);

    public SimpleChannelInboundResponseHandler(String transactionId)
    {
      this.transactionId = transactionId;
    }

    public void reset()
    {
      this.operationCompleted = new CountDownLatch(1);
      this.responseContent = null;
    }

    public String getResponseContent()
    {
      return this.responseContent;
    }

    public boolean isOperationCompleted()
    {
      return this.operationCompleted.getCount() == 0L;
    }

    public boolean sync(long timeout, TimeUnit unit)
    {
      boolean success;
      try
      {
        success = this.operationCompleted.await(timeout, unit);
      }
      catch (InterruptedException e)
      {
         success = false;
      }
      return success;
    }

    public boolean acceptInboundMessage(Object msg)
      throws Exception
    {
      boolean acceptable = super.acceptInboundMessage(msg);
      if (acceptable)
      {
        FullHttpResponse response = (FullHttpResponse)msg;
        String transactionId = response.headers().get("transactionId");
        acceptable = StringUtils.equals(transactionId, this.transactionId);
      }
      return acceptable;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg)
      throws Exception
    {
      this.responseContent = HttpHelper.getBody(msg);
      this.operationCompleted.countDown();
    }
  }



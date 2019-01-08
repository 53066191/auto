  package com.auto.ext.mocker.common.pool;

  import io.netty.buffer.ByteBuf;
  import io.netty.channel.ChannelHandlerContext;
  import io.netty.channel.SimpleChannelInboundHandler;
  import io.netty.handler.codec.http.*;
  import io.netty.handler.timeout.IdleState;
  import io.netty.handler.timeout.IdleStateEvent;
  import io.netty.util.ReferenceCounted;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;

  import java.util.Iterator;
  import java.util.Map;
  import java.util.concurrent.ConcurrentHashMap;

  public class MockerChannelInboundHandler
    extends SimpleChannelInboundHandler<ReferenceCounted>
  {
    private static Logger logger = LoggerFactory.getLogger(MockerChannelInboundHandler.class);
    private Map<String, SimpleChannelInboundHandler> handlerMap = new ConcurrentHashMap();
    private ChannelHandlerContext ctx;

    public void addInboundHandler(String handlerName, SimpleChannelInboundHandler handler)
      throws Exception
    {
      this.handlerMap.put(handlerName, handler);
      handler.handlerAdded(this.ctx);
    }

    public void handlerAdded(ChannelHandlerContext ctx)
      throws Exception
    {
      this.ctx = ctx;
    }

    public boolean acceptInboundMessage(Object msg)
      throws Exception
    {
      return ((msg instanceof ByteBuf)) || ((msg instanceof FullHttpMessage));
    }

    protected void channelRead0(ChannelHandlerContext ctx, ReferenceCounted msg)
      throws Exception
    {
      Iterator<Map.Entry<String, SimpleChannelInboundHandler>> iterator = this.handlerMap.entrySet().iterator();
      while (iterator.hasNext())
      {
        Map.Entry<String, SimpleChannelInboundHandler> entry = (Map.Entry)iterator.next();
        SimpleChannelInboundHandler handler = (SimpleChannelInboundHandler)entry.getValue();
        if (handler.acceptInboundMessage(msg))
        {
          handler.channelRead(ctx, msg.retain());
          if (((handler instanceof SimpleChannelInboundResponseHandler)) && (((SimpleChannelInboundResponseHandler)handler).isOperationCompleted())) {
            iterator.remove();
          }
          return;
        }
      }
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
      throws Exception
    {
      if ((evt instanceof IdleStateEvent))
      {
        IdleStateEvent event = (IdleStateEvent)evt;
        if (event.state() == IdleState.ALL_IDLE) {
          if (ctx.channel().isActive())
          {
            logger.debug("Sending heart beat...");
            FullHttpRequest pingRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "ping");
            ctx.writeAndFlush(pingRequest);
          }
          else
          {
            ctx.channel().close();
          }
        }
      }
    }
  }



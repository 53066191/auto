 package com.auto.ext.mocker.common.pool;

 import io.netty.bootstrap.Bootstrap;
 import io.netty.buffer.PooledByteBufAllocator;
 import io.netty.channel.ChannelInitializer;
 import io.netty.channel.ChannelOption;
 import io.netty.channel.EventLoopGroup;
 import io.netty.channel.pool.ChannelPool;
 import io.netty.channel.socket.SocketChannel;
 import io.netty.channel.socket.nio.NioSocketChannel;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;

 public abstract class AbstractClientChannelPoolFactory
 {
   private static Logger logger = LoggerFactory.getLogger(AbstractClientChannelPoolFactory.class);
   private static ConcurrentMap<String, ChannelPool> channelPoolMap = new ConcurrentHashMap();

   protected abstract ChannelPool newChannelPool(Bootstrap paramBootstrap);

   public ChannelPool getChannelPool(EventLoopGroup eventLoopGroup, String inetHost, Integer remotePort)
   {
     final String poolKey = eventLoopGroup.toString() + ":" + inetHost + ":" + remotePort;
     ChannelPool channelPool = (ChannelPool)channelPoolMap.get(poolKey);
     if (channelPool == null)
     {
       Bootstrap clientBootStrap = new Bootstrap();


       ((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)clientBootStrap.channel(NioSocketChannel.class)).handler(new ChannelInitializer<SocketChannel>()
       {
         protected void initChannel(SocketChannel ch)
           throws Exception
         {
           AbstractClientChannelPoolFactory.logger.info("Connecting to: {}", poolKey);
         }
       }))


         .group(eventLoopGroup))
         .option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true)))
         .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT))
         .remoteAddress(inetHost, remotePort.intValue())
         .connect();

       channelPool = newChannelPool(clientBootStrap);
       ChannelPool oldPool = (ChannelPool)channelPoolMap.putIfAbsent(poolKey, channelPool);
       if (oldPool != null)
       {
         channelPool.close();
         channelPool = oldPool;
       }
     }
     return channelPool;
   }
 }


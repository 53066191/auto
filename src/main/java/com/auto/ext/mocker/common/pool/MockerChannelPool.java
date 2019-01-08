  package com.auto.ext.mocker.common.pool;

  import io.netty.bootstrap.Bootstrap;
  import io.netty.channel.EventLoopGroup;
  import io.netty.channel.nio.NioEventLoopGroup;
  import io.netty.channel.pool.ChannelPoolHandler;
  import io.netty.channel.pool.SimpleChannelPool;

  public class MockerChannelPool
    extends SimpleChannelPool
  {
    private EventLoopGroup executors;

    public MockerChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler)
    {
      super(bootstrap, handler);
      this.executors = new NioEventLoopGroup(1);
    }

    public void close()
    {
      super.close();
      this.executors.shutdownGracefully();
    }
  }



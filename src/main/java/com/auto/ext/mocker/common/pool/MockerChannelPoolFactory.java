package com.auto.ext.mocker.common.pool;

import com.kiktech.ext.mocker.common.httpserver.HttpAdaptiveDecoder;
import com.kiktech.ext.mocker.common.httpserver.HttpAdaptiveEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockerChannelPoolFactory
        extends AbstractClientChannelPoolFactory {
    private static final int HEALTH_CHECK_PERIOD = 60;
    private static final int MAX_MESSAGE_SIZE = 67108864;
    private static Logger logger = LoggerFactory.getLogger(MockerChannelPoolFactory.class);

    protected ChannelPool newChannelPool(Bootstrap clientBootStrap) {
        return new MockerChannelPool(clientBootStrap, new ChannelPoolHandler() {
            public void channelReleased(Channel channel)
                    throws Exception {
                MockerChannelPoolFactory.logger.debug("Channel {} is released.", channel);
            }

            public void channelAcquired(Channel channel)
                    throws Exception {
                MockerChannelPoolFactory.logger.debug("Channel {} is acquired.", channel);
            }

            public void channelCreated(Channel channel)
                    throws Exception {
                MockerChannelPoolFactory.logger.debug("Channel {} is created.", channel);
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast(new ChannelHandler[]{new IdleStateHandler(0, 0, 60)})
                        .addLast(new ChannelHandler[]{new HttpAdaptiveDecoder()})
                        .addLast(new ChannelHandler[]{new HttpAdaptiveEncoder()})
                        .addLast(new ChannelHandler[]{new HttpObjectAggregator(67108864)})
                        .addLast(new ChannelHandler[]{new MockerChannelInboundHandler()});
            }
        });
    }
}



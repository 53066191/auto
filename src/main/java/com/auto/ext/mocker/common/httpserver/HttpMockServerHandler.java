package com.auto.ext.mocker.common.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@ChannelHandler.Sharable
public class HttpMockServerHandler
        extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static Logger logger = LoggerFactory.getLogger(HttpMockServerHandler.class);
    private static final String MENU_CONTENT = "{}";

    protected ChannelFuture writeResponse(FullHttpRequest request, String responseContent, Map<String, String> params, Map<String, String> headers, Channel channel) {
        return writeResponse(request, responseContent, params, headers, HttpResponseStatus.OK, channel);
    }

    protected ChannelFuture writeResponse(FullHttpRequest request, String responseContent, Map<String, String> params, Map<String, String> headers, HttpResponseStatus responseStatus, Channel channel) {
        ByteBuf buf = Unpooled.copiedBuffer(StringUtils.defaultString(responseContent, ""), CharsetUtil.UTF_8);


        boolean close = requireClosing(request);


        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                response.headers().set((String) header.getKey(), header.getValue());
            }
        }
        String transactionSequence = request.headers().get("sequence");
        if (transactionSequence != null) {
            response.headers().set("sequence", transactionSequence);
        }
        String transactionId = (String) params.get("transactionId");
        if (transactionId != null) {
            response.headers().set("transactionId", transactionId);
        }
        addContentTypeIfMissing(responseContent, response.headers());

        if (!close) {
            response.headers().set("Content-Length", Integer.valueOf(buf.readableBytes()));
        }


        if (responseStatus != null) {
            response.setStatus(responseStatus);
        }
        ChannelFuture future = channel.writeAndFlush(response);
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        return future;
    }

    protected void writeMenu(ChannelHandlerContext ctx, FullHttpRequest request) {
        String responseContent = "{}";

        ByteBuf buf = Unpooled.copiedBuffer("{}", CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);


        response.headers().set("Content-Type", "text/plain; charset=UTF-8");
        response.headers().set("Content-Length", Integer.valueOf(buf.readableBytes()));


        ctx.channel().writeAndFlush(response);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.channel().close();
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        writeMenu(ctx, request);
    }

    private void addContentTypeIfMissing(String responseContent, HttpHeaders headers) {
        if (!headers.contains("Content-Type")) {
            String trimResponseContent = StringUtils.trimToEmpty(responseContent);
            if ((StringUtils.startsWith(trimResponseContent, "<")) && (StringUtils.endsWith(trimResponseContent, ">"))) {
                headers.set("Content-Type", "text/xml; charset=UTF-8");
            } else if (((StringUtils.startsWith(trimResponseContent, "[")) && (StringUtils.endsWith(trimResponseContent, "["))) || (
                    (StringUtils.startsWith(trimResponseContent, "{")) && (StringUtils.endsWith(trimResponseContent, "}")))) {
                headers.set("Content-Type", "application/json; charset=UTF-8");
            } else {
                headers.set("Content-Type", "text/plain; charset=UTF-8");
            }
        }
    }

    private boolean requireClosing(FullHttpRequest request) {
        boolean close = (request.headers().contains("Connection", "close", true)) || ((request.getProtocolVersion().equals(HttpVersion.HTTP_1_0)) && (!request.headers().contains("Connection", "keep-alive", true)));
        return close;
    }
}



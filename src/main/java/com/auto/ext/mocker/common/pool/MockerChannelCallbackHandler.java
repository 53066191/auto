package com.auto.ext.mocker.common.pool;

import com.kiktech.ext.mocker.common.httpserver.HttpMockRequest;
import com.kiktech.ext.mocker.common.httpserver.HttpMockServerHandler;
import com.kiktech.ext.mocker.common.httpserver.ResponseClosure;
import com.kiktech.ext.mocker.common.response.exception.MockerStubNotHitException;
import com.kiktech.ext.mocker.common.util.HttpHelper;
import com.kiktech.ext.mocker.common.util.JsonHelper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class MockerChannelCallbackHandler<P>
        extends HttpMockServerHandler {
    private Logger logger = LoggerFactory.getLogger(MockerChannelCallbackHandler.class);
    private ResponseClosure<P> responseClosure;
    private ResponseClosure<P> callbackClosure;
    private String uri;

    public MockerChannelCallbackHandler(String uri, ResponseClosure<P> responseClosure, ResponseClosure<P> callbackClosure) {
        this.responseClosure = responseClosure;
        this.callbackClosure = callbackClosure;
        this.uri = uri;
    }

    public boolean acceptInboundMessage(Object msg)
            throws Exception {
        boolean acceptable = super.acceptInboundMessage(msg);
        if (acceptable) {
            FullHttpRequest request = (FullHttpRequest) msg;
            acceptable = StringUtils.removeStart(request.getUri(), "/").startsWith(this.uri);
        }
        return acceptable;
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        Map<String, String> params = HttpHelper.getParameters(request, false);
        try {
            final P callbackParams = constructCallbackParams(ctx, request);

            Map<String, String> responseHeaders = null;
            if ((callbackParams instanceof HttpMockRequest)) {
                responseHeaders = ((HttpMockRequest) callbackParams).getResponseHeaders();
            } else {
                responseHeaders = new HashMap(2);
            }
            Object responseObject = null;
            try {
                responseObject = callResponseClosure(this.responseClosure, callbackParams, params);
            } catch (MockerStubNotHitException e) {
                responseHeaders.put("STUB_NOT_HIT", "1");
            } catch (Exception e) {
                responseHeaders.put("Mock-Exception", StringUtils.defaultString(e.getMessage(), "Unknown Error"));
            }

            String responseJSONText = (responseObject instanceof String) ? responseObject.toString() : JsonHelper.toSimpleJson(responseObject);
            ChannelFuture channelFuture = writeResponse(request, responseJSONText, params, responseHeaders, ctx.channel());
            if (this.callbackClosure != null) {
                channelFuture.addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future)
                            throws Exception {
                        MockerChannelCallbackHandler.this.callbackClosure.call(callbackParams);
                    }
                });
            }

        } catch (Exception e) {
            this.logger.error("Failed to construct response", e);
        }
    }

    protected Object callResponseClosure(ResponseClosure<P> responseClosure, P callbackParams, Map<String, String> httpParams) {
        return responseClosure.call(callbackParams);
    }

    public abstract P constructCallbackParams(ChannelHandlerContext paramChannelHandlerContext, FullHttpRequest paramFullHttpRequest)
            throws Exception;
}



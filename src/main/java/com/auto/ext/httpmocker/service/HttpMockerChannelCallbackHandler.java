package com.auto.ext.httpmocker.service;

import com.auto.ext.mocker.common.httpserver.HttpMockRequest;
import com.auto.ext.mocker.common.httpserver.ResponseClosure;
import com.auto.ext.mocker.common.pool.MockerChannelCallbackHandler;
import com.auto.ext.mocker.common.util.HttpHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.HashMap;
import java.util.Map;

public class HttpMockerChannelCallbackHandler
        extends MockerChannelCallbackHandler<HttpMockRequest> {
    public HttpMockerChannelCallbackHandler(String uri, ResponseClosure<HttpMockRequest> responseClosure, ResponseClosure<HttpMockRequest> callbackClosure) {
        super(uri, responseClosure, callbackClosure);
    }

    public HttpMockRequest constructCallbackParams(ChannelHandlerContext ctx, FullHttpRequest request)
            throws Exception {
        Map<String, String> params = HttpHelper.getParameters(request, true);
        HttpMockRequest mockRequest = new HttpMockRequest();
        mockRequest.setRequestContent(HttpHelper.getBody(request));
        mockRequest.setRequestParams(params);
        mockRequest.setRequest(request);
        mockRequest.setResponseHeaders(new HashMap());
        return mockRequest;
    }
}

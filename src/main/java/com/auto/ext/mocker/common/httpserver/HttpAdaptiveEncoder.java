package com.auto.ext.mocker.common.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

public class HttpAdaptiveEncoder extends HttpObjectEncoder<HttpMessage> {

    private HttpAdaptiveRequestEncoder requestEncoder = new HttpAdaptiveRequestEncoder();
    private HttpAdaptiveResponseEncoder responseEncoder = new HttpAdaptiveResponseEncoder();

    public boolean acceptOutboundMessage(Object msg)
            throws Exception {
        boolean accept = (this.requestEncoder.acceptOutboundMessage(msg)) || (this.responseEncoder.acceptOutboundMessage(msg));
        return accept;
    }

    protected void encodeInitialLine(ByteBuf buf, HttpMessage message)
            throws Exception {
        if (this.responseEncoder.acceptOutboundMessage(message)) {
            this.responseEncoder.encodeInitialLine(buf, (HttpResponse) message);
        } else {
            this.requestEncoder.encodeInitialLine(buf, (HttpRequest) message);
        }
    }

    private static class HttpAdaptiveRequestEncoder
            extends HttpRequestEncoder {
        public void encodeInitialLine(ByteBuf buf, HttpRequest request)
                throws Exception {
            super.encodeInitialLine(buf, request);
        }
    }

    private static class HttpAdaptiveResponseEncoder
            extends HttpResponseEncoder {
        public void encodeInitialLine(ByteBuf buf, HttpResponse response)
                throws Exception {
            super.encodeInitialLine(buf, response);
        }
    }
}




package ext.mocker.common.util;

import com.kiktech.ext.mocker.common.response.exception.MockerException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHelper {
    public static final String DEFAULT_CHARSET = "UTF-8";
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    public static String getBody(FullHttpMessage message) {
        String content = "";
        ByteBuf byteBuf = message.content().duplicate();
        byteBuf.readerIndex(0);
        int length = byteBuf.readableBytes();
        if (length > 0) {
            String contentType = message.headers().get("Content-Type");
            String charset = StringUtils.defaultString(StringUtils.trimToNull(StringUtils.substringAfter(contentType, "charset=")), "UTF-8");
            byte[] contentBytes = new byte[length];
            byteBuf.readBytes(contentBytes);
            try {
                content = new String(contentBytes, charset);
            } catch (UnsupportedEncodingException e) {
                content = "Unsupported Encoding";
            }
        }
        return content;
    }

    public static Map<String, String> getParameters(FullHttpRequest request, boolean parseBody)
            throws IOException {
        HttpMethod method = request.getMethod();

        Map<String, String> paramMap = new HashMap();

        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            List<String> valueList = (List) entry.getValue();
            String value = StringUtils.join(valueList, ",");
            paramMap.put(entry.getKey(), value);
        }
        String value;
        if ((HttpMethod.POST == method) && (parseBody) &&
                (StringUtils.contains(request.headers().get("Content-Type"), "application/x-www-form-urlencoded"))) {
            FullHttpRequest duplicateRequest = (FullHttpRequest) request.duplicate();
            duplicateRequest.content().readerIndex(0);
            HttpPostRequestDecoder bodyDecoder = new HttpPostRequestDecoder(request);
            bodyDecoder.offer(duplicateRequest);

            List<InterfaceHttpData> paramList = bodyDecoder.getBodyHttpDatas();
            for (InterfaceHttpData param : paramList) {
                Attribute data = (Attribute) param;
                paramMap.put(data.getName(), data.getValue());
            }
        }
        return paramMap;
    }

    public static Map<String, Object> parseQueryString(String uri) {
        Map<String, Object> paramMap = new HashMap();

        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            List<String> valueList = (List) entry.getValue();
            Object value = valueList;
            if (valueList.size() == 1) {
                value = valueList.get(0);
            }
            paramMap.put(entry.getKey(), value);
        }
        return paramMap;
    }

    private static long copyLarge(Reader input, Writer output, char[] buffer)
            throws IOException {
        long count = 0L;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String sendRequest(String sendUrl, Map<String, String> headers, String content, int connectTimeout, int readTimeout)
            throws MockerException {
        StringWriter output = new StringWriter();
        try {
            URL url = new URL(sendUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty((String) header.getKey(), (String) header.getValue());
                }
            }
            byte[] bytesToWrite = content.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(bytesToWrite.length));
            connection.setDoOutput(true);
            connection.setDoInput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            Throwable localThrowable6 = null;
            try {
                out.write(content);
                out.flush();
            } catch (Throwable localThrowable1) {
                localThrowable6 = localThrowable1;
                throw localThrowable1;
            } finally {
                if (out != null) {
                    if (localThrowable6 != null) {
                        try {
                            out.close();
                        } catch (Throwable localThrowable2) {
                            localThrowable6.addSuppressed(localThrowable2);
                        }
                    } else {
                        out.close();
                    }
                }
            }
            InputStreamReader input = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"));
            Throwable localThrowable7 = null;
            long count;
            try {
                count = copyLarge(input, output, new char[4096]);
            } catch (Throwable localThrowable4) {
                localThrowable7 = localThrowable4;
                throw localThrowable4;
            } finally {
                if (input != null) {
                    if (localThrowable7 != null) {
                        try {
                            input.close();
                        } catch (Throwable localThrowable5) {
                            localThrowable7.addSuppressed(localThrowable5);
                        }
                    } else {
                        input.close();
                    }
                }
            }
             connection.disconnect();
            if (count > 2147483647L) {
                return "";
            }
        } catch (Exception e) {
            throw new MockerException(e);
        }
        return output.toString();
    }

    public static String sendRequest(String sendUrl, String content, int connectTimeout, int readTimeout)
            throws MockerException {
        return sendRequest(sendUrl, null, content, connectTimeout, readTimeout);
    }
}



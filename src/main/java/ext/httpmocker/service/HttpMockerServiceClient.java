package ext.httpmocker.service;

import com.alibaba.fastjson.TypeReference;
import com.kiktech.ext.httpmocker.service.exception.MockerServiceException;
import com.kiktech.ext.httpmocker.service.model.MockerRouteEntry;
import com.kiktech.ext.httpmocker.service.model.ResponseTemplate;
import com.kiktech.ext.httpmocker.service.model.ResponseTemplateEntry;
import com.kiktech.ext.mocker.common.httpserver.HttpMockRequest;
import com.kiktech.ext.mocker.common.httpserver.ResponseClosure;
import com.kiktech.ext.mocker.common.pool.MockerChannelInboundHandler;
import com.kiktech.ext.mocker.common.pool.MockerChannelPoolFactory;
import com.kiktech.ext.mocker.common.pool.SimpleChannelInboundResponseHandler;
import com.kiktech.ext.mocker.common.util.CommonUtil;
import com.kiktech.ext.mocker.common.util.HttpHelper;
import com.kiktech.ext.mocker.common.util.IPUtil;
import com.kiktech.ext.mocker.common.util.JsonHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class HttpMockerServiceClient
        extends HttpMockerPlusService {
    private String mockerIp;
    private Integer mockerPort;
    private Integer readTimeout = Integer.valueOf(5000);
    private Integer connectTimeout = Integer.valueOf(5000);
    private EventLoopGroup executors = new NioEventLoopGroup(1);
    private Collection<Integer> remoteHookIdList;
    private ChannelPool mockerChannelPool;
    private static int DEFAULT_PORT = 13000;
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static Logger logger = LoggerFactory.getLogger(HttpMockerServiceClient.class);

    public HttpMockerServiceClient(String mockerIp, Integer mockerPort) {
        this.mockerIp = mockerIp;
        this.mockerPort = mockerPort;
    }

    public Integer getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Integer getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setGlobalVariable(String variableName, String value)
            throws MockerServiceException {
        sendRequest("setGlobalVariable", paramsToString(new Object[]{variableName, value}));
    }

    public void setGlobalLong(String variableName, Long value)
            throws MockerServiceException {
        sendRequest("setGlobalLong", paramsToString(new Object[]{variableName, value}));
    }

    public void setGlobalDouble(String variableName, Double value)
            throws MockerServiceException {
        sendRequest("setGlobalDouble", paramsToString(new Object[]{variableName, value}));
    }

    public void removeGlobalVariable(String variableName)
            throws MockerServiceException {
        sendRequest("removeGlobalVariable", paramsToString(new Object[]{variableName}));
    }

    public <T> T getGlobalVariable(String variableName, Class<T> type)
            throws MockerServiceException {
        String result = getGlobalVariableAsString(variableName);
        return type.isInstance(result) ? (T) result : JsonHelper.parseObject(result, type);
    }

    public String getGlobalVariableAsString(String variableName)
            throws MockerServiceException {
        return sendRequest("getGlobalVariableAsString", paramsToString(new Object[]{variableName}));
    }

    public boolean addRouteEntry(MockerRouteEntry routeEntry)
            throws MockerServiceException {
        String result = sendRequest("addRouteEntry", paramsToString(new Object[]{routeEntry}));
        return Boolean.parseBoolean(result);
    }

    public boolean removeRouteEntry(MockerRouteEntry routeEntry)
            throws MockerServiceException {
        String result = sendRequest("removeRouteEntry", paramsToString(new Object[]{routeEntry}));
        return Boolean.parseBoolean(result);
    }

    public ResponseTemplate addResponseTemplate(ResponseTemplate responseTemplate, Integer aliveTime)
            throws MockerServiceException {
        String result = sendRequest("addResponseTemplate", paramsToString(new Object[]{responseTemplate, aliveTime}));
        return (ResponseTemplate) JsonHelper.parseObject(result, ResponseTemplate.class);
    }

    public ResponseTemplate editResponseTemplate(ResponseTemplate responseTemplate)
            throws MockerServiceException {
        String result = sendRequest("editResponseTemplate", paramsToString(new Object[]{responseTemplate}));
        return (ResponseTemplate) JsonHelper.parseObject(result, ResponseTemplate.class);
    }

    public ResponseTemplate findResponseTemplate(String id)
            throws MockerServiceException {
        String result = sendRequest("findResponseTemplate", paramsToString(new Object[]{id}));
        return (ResponseTemplate) JsonHelper.parseObject(result, ResponseTemplate.class);
    }

    public boolean removeResponseTemplate(String id)
            throws MockerServiceException {
        String result = sendRequest("removeResponseTemplate", paramsToString(new Object[]{id}));
        return Boolean.parseBoolean(result);
    }

    public List<ResponseTemplate> saveResponseTemplateFile(String responseTemplateContent, String targetFileName, boolean sync)
            throws MockerServiceException {
        String result = sendRequest("saveResponseTemplateFile", paramsToString(new Object[]{responseTemplateContent, targetFileName, Boolean.valueOf(sync)}));
        TypeReference resultType = new TypeReference() {
        };
        return (List) JsonHelper.parseObject(result, resultType.getType());
    }

    public List<ResponseTemplate> saveResponseTemplateFiles(List<ResponseTemplateEntry> responseTemplateEntryList, boolean sync)
            throws MockerServiceException {
        String result = sendRequest("saveResponseTemplateFiles", paramsToString(new Object[]{responseTemplateEntryList, Boolean.valueOf(sync)}));
        TypeReference resultType = new TypeReference() {
        };
        return (List) JsonHelper.parseObject(result, resultType.getType());
    }

    public List<ResponseTemplateEntry> syncResponseTemplateStatus(List<ResponseTemplateEntry> responseTemplateEntryList, long syncTime, boolean includesOutOfList)
            throws MockerServiceException {
        String result = sendRequest("syncResponseTemplateStatus", paramsToString(new Object[]{responseTemplateEntryList, Long.valueOf(syncTime), Boolean.valueOf(includesOutOfList)}));
        TypeReference resultType = new TypeReference() {
        };
        return (List) JsonHelper.parseObject(result, resultType.getType());
    }

    public int registerRemoteMock(String mockServiceUrl, String serviceName, String version, String method, String callerIp, String paramExp)
            throws MockerServiceException {
        String result = sendRequest("registerRemoteMock", paramsToString(new Object[]{mockServiceUrl, serviceName, version, method, callerIp, paramExp}));
        return Integer.valueOf(result).intValue();
    }

    public int registerMock(String mockServiceUrl, String serviceName, String domain, String method, Map<String, Object> variableMap, String paramExp)
            throws MockerServiceException {
        String result = sendRequest("registerMock", paramsToString(new Object[]{mockServiceUrl, serviceName, domain, method, variableMap, paramExp}));
        return Integer.valueOf(result).intValue();
    }

    public boolean unregisterRemoteMock(int mockId)
            throws MockerServiceException {
        String result = sendRequest("unregisterRemoteMock", paramsToString(new Object[]{Integer.valueOf(mockId)}));
        return Boolean.parseBoolean(result);
    }

    public int mock(String serviceName, String methodName, String domain, Map<String, Object> variableMap, String paramExp, ResponseClosure<HttpMockRequest> responseClosure)
            throws MockerServiceException {
        return mockInternal(serviceName, methodName, domain, null, variableMap == null ? new HashMap() : variableMap, paramExp, responseClosure, null);
    }

    public int mock(String serviceName, String methodName, String domain, Map<String, Object> variableMap, String paramExp, ResponseClosure<HttpMockRequest> responseClosure, ResponseClosure<HttpMockRequest> callbackClosure)
            throws MockerServiceException {
        return mockInternal(serviceName, methodName, domain, null, variableMap == null ? new HashMap() : variableMap, paramExp, responseClosure, callbackClosure);
    }

    public int mockInternal(String serviceName, String methodName, String domain, String callerIp, Map<String, Object> variableMap, String paramExp, ResponseClosure<HttpMockRequest> responseClosure, ResponseClosure<HttpMockRequest> callbackClosure)
            throws MockerServiceException {
        initChannelPool();
        Integer hookId = null;
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            String fixedServiceName = formatServiceName(serviceName);
            String extendedParamExp = extendParamExp(serviceName, paramExp);
            String combinedKey = fixedServiceName + methodName + domain + callerIp + variableMap + extendedParamExp;
            md5Digest.update(combinedKey.getBytes(CharsetUtil.UTF_8));
            BigInteger bigInteger = new BigInteger(1, md5Digest.digest());
            String mockKey = bigInteger.toString();

            String transactionId = CommonUtil.generateTransactionId();

            Future<Channel> future = this.mockerChannelPool.acquire().sync();
            Channel mockerChannel = (Channel) future.get();
            if (future.isSuccess()) {
                logger.debug("Channel LocalAddress: {}", mockerChannel.localAddress());
                IPUtil.HostAndPort localHostAndPort = IPUtil.getHostAddressAndPort(mockerChannel.localAddress());
                String url = "http://" + IPUtil.localIp().getHostAddress() + ":" + localHostAndPort.port + "/" + mockKey;
                logger.debug("URL to register mock: {}", url);
                boolean useNewAPI = variableMap != null;
                String requestContent = JsonHelper.toSimpleJson(new Object[]{url, fixedServiceName, domain, methodName, useNewAPI ? variableMap : callerIp, extendedParamExp});
                ByteBuf content = Unpooled.copiedBuffer(requestContent, CharsetUtil.UTF_8);
                MockerChannelInboundHandler mockerChannelHandler = (MockerChannelInboundHandler) mockerChannel.pipeline().get(MockerChannelInboundHandler.class);
                SimpleChannelInboundResponseHandler responseHandler = new SimpleChannelInboundResponseHandler(transactionId);
                mockerChannelHandler.addInboundHandler(transactionId, responseHandler);
                mockerChannelHandler.addInboundHandler(mockKey, new HttpMockerChannelCallbackHandler(mockKey, responseClosure, callbackClosure));
                String mockerUri = CommonUtil.generateURI(transactionId, HttpMockerService.class.getName(), useNewAPI ? "registerMock" : "registerRemoteMock");
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, mockerUri, content);
                request.headers().set("Content-Type", "application/json; charset=UTF-8");
                request.headers().set("Content-Length", Integer.valueOf(content.readableBytes()));
                mockerChannel.writeAndFlush(request).sync();
                if ((responseHandler.sync(20L, TimeUnit.SECONDS)) &&
                        (StringUtils.isNumeric(responseHandler.getResponseContent()))) {
                    hookId = Integer.valueOf(responseHandler.getResponseContent());
                    this.remoteHookIdList.add(hookId);
                }
                this.mockerChannelPool.release(mockerChannel);
            } else if (mockerChannel.isActive()) {
                mockerChannel.close();
            }
        } catch (Exception e) {
            logger.error("Error", e);
            throw new MockerServiceException(e);
        }
        return hookId != null ? hookId.intValue() : 0;
    }

    public void close()
            throws MockerServiceException {
        try {
            synchronized (this) {
                if (this.mockerChannelPool != null) {
                    this.mockerChannelPool.close();
                    for (Integer integer : this.remoteHookIdList) {
                        unregisterRemoteMock(integer.intValue());
                    }
                    this.mockerChannelPool = null;
                }
            }
        } catch (Exception e) {
            throw new MockerServiceException(e);
        }
    }

    private String formatServiceName(String serviceName) {
        String fixedUrl = serviceName;
        if (!StringUtils.startsWith(serviceName, "http")) {
            fixedUrl = "http://127.0.0.1/" + StringUtils.removeStart(serviceName, "/");
        }
        String fixedServiceName = serviceName;
        try {
            URL httpUri = new URL(fixedUrl);
            fixedServiceName = StringUtils.substringBefore(StringUtils.removeStart(httpUri.getPath(), "/"), "?");
        } catch (Exception e) {
            logger.error("Invalid ServiceName: {}", serviceName);
        }
        return fixedServiceName;
    }

    private String extendParamExp(String serviceName, String paramExp) {
        String extendedParamExp = paramExp;
        String queryString = StringUtils.substringAfter(serviceName, "?");
        if (StringUtils.isNotEmpty(queryString)) {
            Map<String, Object> paramMap = HttpHelper.parseQueryString("dummy?" + queryString);
            List<String> uriParamExpList = new ArrayList(paramMap.size());
            for (Map.Entry param : paramMap.entrySet()) {
                uriParamExpList.add("params['" + param.getKey() + "'] == '" + param.getValue() + "'");
            }
            String uriParamExtraExp = "if (!(" + StringUtils.join(uriParamExpList, " && ") + "))" + "{return false};\r\n";
            extendedParamExp = uriParamExtraExp + (String) StringUtils.defaultIfEmpty(extendedParamExp, "return true");
        }
        return extendedParamExp;
    }

    private void initChannelPool() {
        if (this.mockerChannelPool == null) {
            synchronized (this) {
                if (this.mockerChannelPool == null) {
                    try {
                        this.remoteHookIdList = new ConcurrentLinkedQueue();
                        MockerChannelPoolFactory mockerChannelPoolFactory = new MockerChannelPoolFactory();
                        this.mockerChannelPool = mockerChannelPoolFactory.getChannelPool(this.executors, this.mockerIp, this.mockerPort);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private String sendRequest(String methodName, String content)
            throws MockerServiceException {
        String mockerUrl = "http://" + this.mockerIp + ":" + this.mockerPort + "/" + "mockerAdmin" + "/" + methodName;
        StringWriter output = new StringWriter();
        try {
            URL url = new URL(mockerUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(this.connectTimeout.intValue());
            connection.setReadTimeout(this.readTimeout.intValue());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
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
            long count = 0L;
            InputStreamReader input = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"));
            Throwable localThrowable7 = null;
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
            throw new MockerServiceException(e);
        }
        return output.toString();
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

    private static String paramsToString(Object... params) {
        return JsonHelper.toSimpleJson(params);
    }
}




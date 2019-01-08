package com.auto.base

import com.auto.ext.httpmocker.service.HttpMockerServiceClient
import com.auto.ext.httpmocker.service.exception.MockerServiceException
import com.auto.util.GroovyJDBC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.annotations.AfterMethod

class TestCaseBase {

    public static Logger logger = LoggerFactory.getLogger("TEST");
    public static String mockerIp = ""
    public static int mockerPort ;
    public static String forkLiftBaseUrl = "";
    public static String taskDistributeUrl = "";
    public static String picUrl=""

    private static Properties props = ActiveProperties.getInstance().props;

    public static HttpMockerServiceClient httpMockerClient;


    static {
        initService()
    }

    public static void initService() {

        logger.info("+++++++ 初始化环境 +++++++++");
        forkLiftBaseUrl = String.format("http://%s:%s", props.getProperty("swarm.forklift.ip"), props.getProperty("swarm.forklift.port"));
        taskDistributeUrl =  String.format("http://%s:%s", props.getProperty("task.distribute.ip"), props.getProperty("task.distribute.port"));

        mockerIp = props.getProperty("http.mocker.ip");
        mockerPort = Integer.valueOf(props.getProperty("http.mocker.port"));
        String httpServerPort = props.getProperty("http.server.port");
        picUrl = String.format("http://%s:%s/pic/", mockerIp, httpServerPort)
    }

    public static HttpMockerServiceClient getHttpMockerClient(){
        if(!httpMockerClient){
            httpMockerClient =  new HttpMockerServiceClient(mockerIp, mockerPort)
        }
        return httpMockerClient


    }


    public static GroovyJDBC getForkliftJdbc(){
        String url = props.getProperty("swarm.forklift.db.url");
        String username = props.getProperty("swarm.forklift.db.user");
        String password = props.getProperty("swarm.forklift.db.password");
        return new GroovyJDBC(url, username, password);
    }

    public static GroovyJDBC getTaskDistributeJdbc(){
        String url = props.getProperty("task.distribute.db.url");
        String username = props.getProperty("task.distribute.db.user");
        String password = props.getProperty("task.distribute.db.password");
        return new GroovyJDBC(url, username, password);
    }




    @AfterMethod
    public void teardownHttpMocker() {
        if (!(httpMockerClient == null)) {
            logger.info("销毁mock客户端");
            try {
                httpMockerClient.close();
            } catch (MockerServiceException e) {
                e.printStackTrace();
            }
        }
    }
}

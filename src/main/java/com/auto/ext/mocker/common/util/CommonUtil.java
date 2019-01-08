package com.auto.ext.mocker.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

public class CommonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    public static String generateTransactionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String generateURI(String transactionId, String serviceName, String mockerMethod) {
        String uri = "/mockerAdmin/" + mockerMethod + "?" + "serviceName" + "=" + serviceName + "&" + "method" + "=" + mockerMethod + "&" + "transactionId" + "=" + transactionId;
        return uri;
    }

    public static Properties getResource(String resource) {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = CommonUtil.class.getClassLoader().getResourceAsStream(resource);
            if (is != null) {
                props.load(is);
            }
            return props;
        } catch (IOException e) {
            LOGGER.info("Failed to load resource from: " + resource, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                LOGGER.info("Failed to close resource: " + resource, e);
            }
        }
        return props;
    }

    public static String getZkclient() {
        return getResource("properties/test/application-test.properties").getProperty("VIP_CFGCENTER_ZK_CONNECTION");
    }

    public static String getProperty(String propertyName) {
        return getProperty(propertyName, null);
    }

    public static String getProperty(String propertyName, String defaultValue) {
        String value = System.getProperty(propertyName);
        if (value == null) {
            value = System.getenv(propertyName);
        }
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static boolean isOSWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.startsWith("windows");
    }

    public static boolean isOSLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.equals("linux");
    }

    public static void disableHermesValidator() {
        System.setProperty("validate-internal.start", "false");
    }
}



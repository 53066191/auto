package com.auto.ext.httpmocker.service.model;

public class MockerRouteEntry  implements Comparable<MockerRouteEntry> {
    private String serviceName;
    private String version;
    private String methodName;
    private String callerIp;
    private String callerPort;
    private String calleeIp;
    private String hostName;
    private Integer calleePort;
    private Long timeout;
    private Integer priority;

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCallerIp() {
        return this.callerIp;
    }

    public void setCallerIp(String callerIp) {
        this.callerIp = callerIp;
    }

    public String getCallerPort() {
        return this.callerPort;
    }

    public void setCallerPort(String callerPort) {
        this.callerPort = callerPort;
    }

    public String getCalleeIp() {
        return this.calleeIp;
    }

    public void setCalleeIp(String calleeIp) {
        this.calleeIp = calleeIp;
    }

    public Integer getCalleePort() {
        return this.calleePort;
    }

    public void setCalleePort(Integer calleePort) {
        this.calleePort = calleePort;
    }

    public Long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getHostName() {
        String hostName = this.hostName;
        if (hostName == null) {
            hostName = getCalleeIp();
        }
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int compareTo(MockerRouteEntry another) {
        if (another == null) {
            return 1;
        }
        Integer priority1 = Integer.valueOf(this.priority != null ? this.priority.intValue() : 0);
        Integer priority2 = Integer.valueOf(another.priority != null ? another.priority.intValue() : 0);
        return priority2.compareTo(priority1);
    }
}


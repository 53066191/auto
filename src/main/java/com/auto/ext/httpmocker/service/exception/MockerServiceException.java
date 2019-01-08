package com.auto.ext.httpmocker.service.exception;

public class MockerServiceException  extends Exception {
    private static final long serialVersionUID = 1236363678676L;

    public MockerServiceException(String message) {
        super(message);
    }

    public MockerServiceException(String message, Throwable e) {
        super(message, e);
    }

    public MockerServiceException(Throwable e) {
        super(e);
    }
}


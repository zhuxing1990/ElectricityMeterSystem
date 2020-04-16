package com.vunke.electricity.server.config;

/**
 * Created by Administrator on 2018-03-24.
 */
public class PermissionException extends RuntimeException {

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionException(Throwable cause) {
        super(cause);
    }
}

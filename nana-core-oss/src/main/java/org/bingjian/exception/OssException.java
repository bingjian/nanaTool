package org.bingjian.exception;

import com.obs.services.exception.ObsException;

import java.util.Observable;

public class OssException extends ObsException {

    public OssException(String message) {
        super(message);
    }

    public OssException(String message, Throwable e) {
        super(message, e);
    }

    public OssException(String message, String xmlMessage) {
        super(message, xmlMessage);
    }

    public OssException(String message, String xmlMessage, Throwable cause) {
        super(message, xmlMessage, cause);
    }
}

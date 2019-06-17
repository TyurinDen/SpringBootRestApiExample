package com.websystique.springboot.service.vkInfoBotClasses.exceptions;

public class NoSuchObjectFoundException extends RuntimeException {
    public NoSuchObjectFoundException(String message) {
        super(message);
    }

    public NoSuchObjectFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

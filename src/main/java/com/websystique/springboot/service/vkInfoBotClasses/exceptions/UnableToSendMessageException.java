package com.websystique.springboot.service.vkInfoBotClasses.exceptions;

public class UnableToSendMessageException extends RuntimeException {
    public UnableToSendMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToSendMessageException(String message) {
        super(message);
    }
}

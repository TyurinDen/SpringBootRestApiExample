package com.websystique.springboot.service.vkInfoBotClasses.exceptions;

public class UnableToSendMessageException extends Exception {
    public UnableToSendMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToSendMessageException(String message) {
        super(message);
    }
}

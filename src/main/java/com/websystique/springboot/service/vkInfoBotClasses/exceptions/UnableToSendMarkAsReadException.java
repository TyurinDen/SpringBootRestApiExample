package com.websystique.springboot.service.vkInfoBotClasses.exceptions;

public class UnableToSendMarkAsReadException extends RuntimeException {
    public UnableToSendMarkAsReadException(String message) {
        super(message);
    }

    public UnableToSendMarkAsReadException(String message, Throwable cause) {
        super(message, cause);
    }
}

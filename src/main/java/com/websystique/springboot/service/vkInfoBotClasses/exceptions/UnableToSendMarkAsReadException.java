package com.websystique.springboot.service.vkInfoBotClasses.exceptions;

public class UnableToSendMarkAsReadException extends Exception {
    public UnableToSendMarkAsReadException(String message) {
        super(message);
    }

    public UnableToSendMarkAsReadException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.websystique.springboot.service.vkInfoBotClasses.errors;

public class ResponseFromApiVk {
    private int response;

    public int getResponse() {
        return response;
    }

    @Override //TODO 11-06-19 убрать!
    public String toString() {
        return "ResponseFromApiVk{" +
                "response=" + response +
                '}';
    }
}

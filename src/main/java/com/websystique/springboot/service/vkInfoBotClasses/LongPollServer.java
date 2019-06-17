package com.websystique.springboot.service.vkInfoBotClasses;

public class LongPollServer {
    private String key;
    private String server;
    private int ts;

    public LongPollServer(String key, String server, int ts) {
        this.key = key;
        this.server = server;
        this.ts = ts;
    }

    public String getKey() {
        return key;
    }

    public String getServer() {
        return server;
    }

    public int getTs() {
        return ts;
    }

}

package com.websystique.springboot.configs.impl;

import com.websystique.springboot.configs.InfoBotConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "file:./vk-info-bot.properties", encoding = "UTF-8")
public class InfoBotConfigImpl implements InfoBotConfig {
    private String vkInfoBotClubId;
    private String vkVkInfoBotAccessToken;
    private String vkApiUrl;
    private String vkApiVersion;
    private static Logger logger = LoggerFactory.getLogger(InfoBotConfigImpl.class.getName());

    @Autowired
    public InfoBotConfigImpl(Environment env) {
        try {
            this.vkInfoBotClubId = env.getProperty("vk.info-bot.id");
            logger.info("vkInfoBotClubId: {}", vkInfoBotClubId);
            this.vkVkInfoBotAccessToken = env.getProperty("vk.info-bot.access_token");
            logger.info("vkVkInfoBotAccessToken: {}", vkVkInfoBotAccessToken);
            this.vkApiUrl = env.getProperty("vk.apiUrl");
            this.vkApiVersion = env.getProperty("vk.api.version");
        } catch (IllegalStateException ise) {
            logger.error("Vk InfoBot is not initialized. Check vk-info-bot.properties file", ise);
        }
    }

    @Override
    public String getVkInfoBotClubId() {
        return vkInfoBotClubId;
    }

    @Override
    public String getVkInfoBotAccessToken() {
        return vkVkInfoBotAccessToken;
    }

    @Override
    public String getVkApiUrl() {
        return vkApiUrl;
    }

    @Override
    public String getVkApiVersion() {
        return vkApiVersion;
    }

}

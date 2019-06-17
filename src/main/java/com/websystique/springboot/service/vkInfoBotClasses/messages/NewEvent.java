package com.websystique.springboot.service.vkInfoBotClasses.messages;

import com.google.gson.annotations.SerializedName;
import com.websystique.springboot.service.vkInfoBotClasses.messages.Message;

public class NewEvent { //VkInfoBotUpdatesObj, InfoBotUpdatesObj, BotUpdatesObj, InfoBotNewEvent, VkInfoBotNewEvent
    private String type;

    @SerializedName("object")
    private Message message; //TODO несоответствие имени объекта и поля JSON

    @SerializedName("group_id")
    private int groupId;

    public String getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }

    public int getGroupId() {
        return groupId;
    }

    @Override //TODO убрать после отладки
    public String toString() {
        return "NewEvent{" +
                "type='" + type + '\'' +
                ", message=" + message +
                ", groupId=" + groupId +
                '}';
    }
}

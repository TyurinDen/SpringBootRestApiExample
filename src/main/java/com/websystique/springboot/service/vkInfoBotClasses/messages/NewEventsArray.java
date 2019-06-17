package com.websystique.springboot.service.vkInfoBotClasses.messages;

import com.google.gson.annotations.SerializedName;
import com.websystique.springboot.service.vkInfoBotClasses.messages.NewEvent;

import java.util.ArrayList;
import java.util.List;

public class NewEventsArray {
    private int ts;

    @SerializedName("updates")
    private List<NewEvent> newEventsList = new ArrayList<>();

    public int getTs() {
        return ts;
    }

    public List<NewEvent> getNewEventsList() {
        return newEventsList;
    }

    @Override //TODO убрать после отладки
    public String toString() {
        return "NewEventsArray{" +
                "ts=" + ts +
                ", newEventsList=" + newEventsList.toString() +
                '}';
    }
}

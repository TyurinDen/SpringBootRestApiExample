package com.websystique.springboot.service.vkInfoBotClasses.messages;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Message { // TODO: 20.06.2019 удалить неиспользующиеся методы
    private int date;

    @SerializedName("from_id")
    private int fromId;

    private int id;

    private int out;
    @SerializedName("peer_id")
    private int peerId;

    private String text;

    @SerializedName("conversation_message_id")
    private int convMessagesId;

    @SerializedName("fwd_messages")
    private List<FwdMessages> fwdMessagesList = new ArrayList<>();

    private boolean important;

    @SerializedName("random_id")
    private int randomId;

    // Заглушка, использоваться не будет, так как бот с вложениями не работает, только с текстом сообщения
    @SerializedName("attachments")
    private List attachmentsList;

    @SerializedName("is_hidden")
    private boolean isHidden;

    public int getDate() {
        return date;
    }

    public int getFromId() {
        return fromId;
    }

    public int getId() {
        return id;
    }

    public int getOut() {
        return out;
    }

    public int getPeerId() {
        return peerId;
    }

    public String getText() {
        return text;
    }

    public int getConvMessagesId() {
        return convMessagesId;
    }

    public List<FwdMessages> getFwdMessagesList() {
        return fwdMessagesList;
    }

    public boolean getIsImportant() {
        return important;
    }

    public int getRandomId() {
        return randomId;
    }

    public List<Object> getAttachmentsList() {
        return attachmentsList;
    }

    public boolean getIsHidden() {
        return isHidden;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return date == message.date &&
                fromId == message.fromId &&
                id == message.id &&
                out == message.out &&
                peerId == message.peerId &&
                convMessagesId == message.convMessagesId &&
                important == message.important &&
                randomId == message.randomId &&
                isHidden == message.isHidden &&
                Objects.equals(text, message.text) &&
                Objects.equals(fwdMessagesList, message.fwdMessagesList) &&
                Objects.equals(attachmentsList, message.attachmentsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, fromId, id, out, convMessagesId, randomId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "date=" + date +
                ", fromId=" + fromId +
                ", id=" + id +
                ", out=" + out +
                ", peerId=" + peerId +
                ", text='" + text + '\'' +
                ", convMessagesId=" + convMessagesId +
                ", fwdMessagesList=" + fwdMessagesList +
                ", important=" + important +
                ", randomId=" + randomId +
                ", attachmentsList=" + attachmentsList +
                ", isHidden=" + isHidden +
                '}';
    }
}

package com.websystique.springboot.service.vkInfoBotClasses.messages;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
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
    private List<FwdMessages> fwdMessagesList;

    private boolean important;

    @SerializedName("random_id")
    private int randomId;

    // Заглушка, использоваться не будет, так как бот с вложениями не работает, только с текстом сообщения
    @SerializedName("attachments")
    private List attachmentsList;

    @SerializedName("is_hidden")
    private boolean isHidden;

}

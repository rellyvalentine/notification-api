package com.valentine.demo.entities.messaging;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private long messageId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "date")
    private Timestamp date;

    @Column(name = "is_new")
    private boolean isNew;

    public Message() {

    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}

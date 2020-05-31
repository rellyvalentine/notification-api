package com.valentine.demo.entities.messaging;

public class UserChat {

    private long userId;
    private long chatId;

    public UserChat(long userId, long chatId) {
        this.userId = userId;
        this.chatId = chatId;
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
}

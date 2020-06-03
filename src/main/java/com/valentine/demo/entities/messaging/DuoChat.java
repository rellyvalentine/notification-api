package com.valentine.demo.entities.messaging;

import com.valentine.demo.entities.UserAccount;

public class DuoChat extends Chat{

    private UserAccount otherUser;

    public UserAccount getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(UserAccount otherUser) {
        this.otherUser = otherUser;
    }
}

package com.valentine.demo.entities.messaging;

import com.valentine.demo.entities.UserAccount;

import javax.persistence.*;
import java.util.List;


@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "topic")
    private String topic;

    @Column(name = "password")
    private String password;

    //this field represents the USERS in a single CHAT
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "user_chat", joinColumns=@JoinColumn(name="chat_id"), inverseJoinColumns=@JoinColumn(name="user_id"))
    private List<Message> users;

//    //@Transient lets us add fields that aren't stored in the database
//    @Transient
//    private UserAccount otherUser; //this user field will be used for Duo Conversations

    public Chat() {

    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public UserAccount getOtherUser() {
//        return otherUser;
//    }
//
//    public void setOtherUser(UserAccount otherUser) {
//        this.otherUser = otherUser;
//    }
}

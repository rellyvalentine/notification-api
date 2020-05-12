package com.valentine.demo.entities.messaging;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;


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
}

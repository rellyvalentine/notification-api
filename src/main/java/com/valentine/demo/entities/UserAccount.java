package com.valentine.demo.entities;

import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.entities.messaging.Message;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user_accounts")
public class UserAccount {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "username")
    private String userName;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    private String role;

    //this field represents the CHATS that a single USER is in
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @JoinTable(name = "user_chat", joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="chat_id"))
    private List<Chat> chats;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

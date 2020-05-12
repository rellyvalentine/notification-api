package com.valentine.demo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Notification {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private long id;

    @Column(name = "head")
    @JsonProperty("head")
    private String head;

    @Column(name = "body")
    @JsonProperty("body")
    private String body;

    @Column(name = "date")
    @JsonProperty("date")
    private Timestamp date;

    //    @JsonProperty("is_new")
    @Column(name = "is_new")
    private boolean isNew;

    @Column(name = "user_id")
    @JsonProperty("userId")
    private long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getDate() {
        return this.date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        this.isNew = aNew;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}

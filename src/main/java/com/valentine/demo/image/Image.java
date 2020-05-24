package com.valentine.demo.image;

import javax.persistence.*;

@Entity
@Table(name = "profile_picture")
public class Image {

    @Id
    @Column(name = "user_id")
    private long id;

    @Column(name = "location")
    private String location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

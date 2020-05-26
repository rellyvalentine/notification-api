package com.valentine.demo.dto;

import org.springframework.web.multipart.MultipartFile;

public class UserPfpDTO {
    private MultipartFile file;
    private String userName;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

package com.valentine.demo.dao;

import com.valentine.demo.entities.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UserCreator {
    @Autowired
    private UserAccountRepository accountRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void init(){
        UserAccount user1 = new UserAccount();
        user1.setUserName("bart");
        user1.setName("bart");
        user1.setPassword(passwordEncoder.encode("password1"));
        user1.setPfp("/profile-pictures/img1.png");
        user1.setRole("USER");

        UserAccount user2 = new UserAccount();
        user2.setUserName("liluziburner");
        user2.setName("baby pluto");
        user2.setPassword(passwordEncoder.encode("password2"));
        user2.setPfp("/profile-pictures/img2.png");
        user2.setRole("USER");

        accountRepo.save(user1);
        accountRepo.save(user2);
    }
}

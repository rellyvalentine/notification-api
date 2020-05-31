package com.valentine.demo.services.messaging;

import com.valentine.demo.dao.messaging.ChatRepository;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.entities.messaging.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;

@Service
public class UserChatService {

    @Autowired
    ChatRepository chatRepo;

    @PersistenceContext
    EntityManager entityManager;

    //insert our users into the chat
    @Transactional
    public void addUsers(Long chat, List<Long> users){

        //add each user to the chat
        for(Long user : users){
            entityManager.createNativeQuery("INSERT INTO user_chat VALUES (?, ?)")
                    .setParameter(1, chat)
                    .setParameter(2, user);
        }

    }

    //Get all the chats of the logged in user
    public List<Chat> getChatsByUserId(long userId) {
        return chatRepo.getChatsByUserId(userId);
    }

}

package com.valentine.demo.services.messaging;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dao.messaging.ChatRepository;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
public class UserChatService {

    @Autowired
    ChatRepository chatRepo;

    @Autowired
    UserAccountService accountService;

    @PersistenceContext
    EntityManager entityManager;

    //List of all chats from the user
    //Transform the list into a map that contains a list of users
    //find the chat that contains the list with those users

    //insert our users into the chat
    @Transactional
    public void addUsers(Long chat, List<Long> users){
        users.sort(Comparator.naturalOrder());
        //check if the chat already exists
        if(checkIfExists(users)){
            DemoApplication.logger.debug("These users are already in a chat together");
        } else {

            //add each user to the chat
            for (Long user : users) {
                DemoApplication.logger.debug("Adding user: " + accountService.getUserById(user).getUserName());
                entityManager.createNativeQuery("INSERT INTO user_chat VALUES (?, ?)")
                        .setParameter(1, chat)
                        .setParameter(2, user)
                        .executeUpdate();
            }
        }

    }

    //Get all the chats of the logged in user
    public List<Chat> getChatsByUserId(long userId){
        return chatRepo.getChatsByUserId(userId);
    }

    protected List<Long> getUsersInChat(long chatId){
        return chatRepo.getUsersInChat(chatId);
    }

    public boolean checkIfExists(List<Long> addedUsers){
        List<Chat> chats = getChatsByUserId(accountService.getLoggedInUserAccount().getUserId());
        Map<Long, List<Long>> userChatMap = new HashMap<>();
        for(Chat chat : chats){
            List<Long> usersInChat = getUsersInChat(chat.getChatId());
            usersInChat.sort(Comparator.naturalOrder()); //sort the users in the chat
            userChatMap.put(chat.getChatId(), usersInChat);
        }
        return userChatMap.containsValue(addedUsers);
    }

}

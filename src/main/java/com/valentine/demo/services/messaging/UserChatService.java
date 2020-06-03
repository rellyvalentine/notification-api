package com.valentine.demo.services.messaging;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dao.messaging.ChatRepository;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserChatService {

    @Autowired
    ChatRepository chatRepo;

    @Autowired
    MessagingService msgService;

    @Autowired
    UserAccountService accountService;

    @PersistenceContext
    EntityManager entityManager;

    //List of all chats from the user
    //Transform the list into a map that contains a list of users
    //find the chat that contains the list with those users

    //insert our users into the chat
    @Transactional
    public void addUsers(List<Long> users){
        users.sort(Comparator.naturalOrder());
        //check if the chat already exists
        if(checkIfExists(users)){
            DemoApplication.logger.debug("These users are already in a chat together");
        } else {

            Chat chat = new Chat();
            if(users.size() == 2){
                chat.setGroupChat(false);
            }
            msgService.createNewChat(chat); //save the chat to the database

            //add each user to the chat
            for (Long user : users) {
                DemoApplication.logger.debug("Adding user: " + accountService.getUserById(user).getUserName());
                entityManager.createNativeQuery("INSERT INTO user_chat VALUES (?, ?)")
                        .setParameter(1, chat.getChatId())
                        .setParameter(2, user)
                        .executeUpdate();
            }
        }
    }

    //Get all the chats of the logged in user
    public List<Chat> getChatsByUserId(long userId){

        List<Chat> chats = chatRepo.getChatsByUserId(userId);
        List<Chat> duoChats = chats.stream()
                .filter(chat -> !chat.isGroupChat()) //filter out Group Chats
                .collect(Collectors.toList());

        for(Chat chat : duoChats){
            //get the other user from the chat
            long otherUser = getUsersInChat(chat.getChatId()).stream()
                    .filter(user -> user != accountService.getLoggedInUserAccount().getUserId())
                    .collect(Collectors.toList()).get(0);
            chat.setOtherUser(accountService.getUserById(otherUser));
        }
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

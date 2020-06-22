package com.valentine.demo.services.messaging;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dao.messaging.MessageStore;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.entities.messaging.Message;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessagingService {

    @Autowired
    MessageStore messageStore;

    @Autowired
    UserChatService chatService;

    @Autowired
    UserAccountService accountService;

    @Autowired
    SimpMessageSendingOperations sendingOperations;

    @PersistenceContext
    EntityManager entityManager;

    public void saveMessage(Message message){

        ZonedDateTime current = ZonedDateTime.now(ZoneId.of("America/New_York")); //gives us the current date from time zone

        //format the current time for the sql timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String currentDateString = current.format(formatter);
//        DemoApplication.logger.debug(currentDateString); //for testing

        Timestamp sqlDate = Timestamp.valueOf(currentDateString); //convert to sql

        message.setDate(sqlDate);
        message.setNew(true);

        messageStore.save(message);

//        chatService.getChatById(message.getChatId()).setMostRecent(message);
    }

    //WebSocket live sending functionality
    public Message sendMessage(Message message){
        message.setReceived(true);
        String otherUser = chatService.getChatById(message.getChatId())
                .getOtherUser().getUserName();
        //send the other user the message where received = true
        sendingOperations.convertAndSendToUser(otherUser, "/queue/receive-message", message);
        message.setReceived(false);
        DemoApplication.logger.debug("MESSAGE HAS BEEN SENT: "+message.getMessageId());
        return message;
    }

    //user getting their own messages
    public List<Message> getChatMessages(long chatId){
        long currentUser = accountService.getLoggedInUserAccount().getUserId();
        List<Message> chatMessages =  messageStore.getMessagesByChatId(chatId);
        for(Message message : chatMessages) {
            if(message.getUserId() != currentUser){
                message.setReceived(true);
            }
        }
        return chatMessages;
    }

    //creating new message notification for another user
    public List<Message> getChatMessages(long chatId, long userId){
        List<Message> chatMessages = messageStore.getMessagesByChatId(chatId);

        for(Message message : chatMessages){
            if(message.getUserId() != userId){
                message.setReceived(true);
            }
        }
        return chatMessages;
    }

    //getting RETRIEVED messages that are NEW
    private List<Message> getNewMessages(long chatId){

        return getChatMessages(chatId).stream()
                .filter(Message::isReceived)
                .filter(Message::isNew)
                .collect(Collectors.toList());
    }

    //getting RETRIEVED messages that are NEW
    private List<Message> getNewMessages(long chatId, long userId){

        return getChatMessages(chatId, userId).stream()
                .filter(Message::isReceived)
                .filter(Message::isNew)
                .collect(Collectors.toList());
    }

    public List<Message> getAllNewMessages(long userId){
        List<Chat> chats = chatService.getChatsByUserId(userId);
        List<Message> newMessages = new ArrayList<>();

        boolean loggedIn = accountService.getLoggedInUserAccount().getUserId() == userId;

        for(Chat chat : chats){ //add the new messages from each chat

            if(loggedIn){ //logged in user getting new messages

//                DemoApplication.logger.debug("Retrieving own new messages");
                newMessages.addAll(getNewMessages(chat.getChatId()));
            } else{ //user sending a new message notification

//                DemoApplication.logger.debug("Sending new message notification");
               newMessages.addAll(getNewMessages(chat.getChatId(), userId));
            }
        }
//        DemoApplication.logger.debug(accountService.getUserById(userId).getUserName()+" notifications: "+newMessages.size());
        return newMessages;
    }

    @Transactional
    public void readMessages(long chatId){
        List<Message> newMessages = getNewMessages(chatId);
        for(Message message : newMessages){
            entityManager.createNativeQuery("UPDATE message SET is_new = false WHERE message_id = ?;")
                    .setParameter(1, message.getMessageId())
                    .executeUpdate();
        }
    }

    public Message getRecent(long chatId){
        List<Message> messages = getChatMessages(chatId);
        return messages.get(messages.size()-1);
    }

}

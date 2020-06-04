package com.valentine.demo.services.messaging;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dao.messaging.MessageStore;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.entities.messaging.Message;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public void saveMessage(Message message){

        ZonedDateTime current = ZonedDateTime.now(ZoneId.of("America/New_York")); //gives us the current date from time zone

        //format the current time for the sql timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String currentDateString = current.format(formatter);
        DemoApplication.logger.debug(currentDateString); //for testing

        Timestamp sqlDate = Timestamp.valueOf(currentDateString); //convert to sql

        message.setDate(sqlDate); //set the notification to the current time in SQL
        message.setNew(true);

        messageStore.save(message);
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

    //loading send messages
    public List<Message> getChatMessages(long chatId){
        long currentUser = accountService.getLoggedInUserAccount().getUserId();
        List<Message> chatMessages =  messageStore.getMessagesByChatId(chatId);
        for(Message message : chatMessages) {
            if(message.getUserId() == currentUser){
                message.setReceived(false);
            }
        }
        return chatMessages;
    }

}

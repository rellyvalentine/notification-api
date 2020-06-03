package com.valentine.demo.services.messaging;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dao.messaging.MessageStore;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.entities.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MessagingService {

    @Autowired
    MessageStore messageStore;

    @Autowired
    UserChatService chatService;

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

    public void sendMessage(Message message){
        Chat currentChat = chatService.getChatById(message.getChatId());

    }

    public List<Message> getChatMessages(long chatId){
        return messageStore.getMessagesByChatId(chatId);
    }

}

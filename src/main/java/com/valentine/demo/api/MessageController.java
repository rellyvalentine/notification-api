package com.valentine.demo.api;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.entities.messaging.Message;
import com.valentine.demo.services.UserAccountService;
import com.valentine.demo.services.messaging.MessagingService;
import com.valentine.demo.services.messaging.UserChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    MessagingService msgService;

    @Autowired
    UserAccountService accountService;

    @Autowired
    UserChatService userChatService;

    @Autowired
    SimpMessageSendingOperations sendingOperations;

//    private Chat openChat;

    //this endpoint will be used with the WebSocket
//    @GetMapping("/api-v1/user-chats")
//    public List<Chat> userChats(){
//        //get the chats of the currently logged in user
//        return userChatService.getChatsByUserId(accountService.getLoggedInUserAccount().getUserId());
//    }

    //this service will be used with the WebSocket
    @MessageMapping("/search-users")
    @SendToUser("/topic/found-users")
    public List<UserAccount> searchUsers(String s){
        return accountService.searchForUser(s);
    }

    @MessageMapping("/open-chat")
    @SendToUser("/topic/chat")
    public Chat openChat(long chatId){
        Chat openChat = userChatService.getChatById(chatId);
//        DemoApplication.logger.debug("User in chat: "+openChat.getOtherUser());
//        return msgService.getChatMessages(id);
//        return userChatService.getChatById(id);
        readMessage(openChat.getChatId());
        return openChat;
    }

    @GetMapping("/api-v1/chat-messages")
    public List<Message> loadMessages(@RequestParam long chatId){
        return msgService.getChatMessages(chatId);
    }

    @MessageMapping("/send")
    @SendToUser("/queue/sent-message")
    @RequestMapping("/queue/receive-message")
    public Message sendMessage(@Payload Message message){
        long senderId = accountService.getLoggedInUserAccount().getUserId();
        message.setUserId(senderId);
        msgService.saveMessage(message);

        createMessageNotification(message.getChatId());

        return msgService.sendMessage(message);
    }


    @RequestMapping("/queue/message-new")
    public void createMessageNotification(long chatId){
        long otherUser = userChatService.getChatById(chatId).getOtherUser().getUserId();

//        long otherUser = openChat.getOtherUser().getUserId();
        DemoApplication.logger.debug("Other User: "+accountService.getUserById(otherUser).getUserName());
        int newMessages = msgService.getAllNewMessages(otherUser).size();

        sendingOperations.convertAndSendToUser(accountService.getUserById(otherUser).getUserName(), "/queue/message-new", newMessages);
    }

    @RequestMapping("/queue/read-message")
    public void readMessage(long chatId){
        msgService.readMessages(chatId);

//        msgService.readMessages(openChat.getChatId());
        long userId = accountService.getLoggedInUserAccount().getUserId();
        int newMessages = msgService.getAllNewMessages(userId).size();

        sendingOperations.convertAndSendToUser(accountService.getLoggedInUserAccount().getUserName(),
                "/queue/read-message",
                newMessages);
    }


//    public Message sentMessage(Message message){
//        DemoApplication.logger.debug("MESSAGE HAS BEEN SENT: "+message.getMessageId());
//        return message;
//    }


}

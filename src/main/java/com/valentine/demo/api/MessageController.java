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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    //this endpoint will be used with the WebSocket
    @GetMapping("/api-v1/user-chats")
    public List<Chat> userChats(){
        //get the chats of the currently logged in user
        return userChatService.getChatsByUserId(accountService.getLoggedInUserAccount().getUserId());
    }

    //this service will be used with the WebSocket
    @MessageMapping("/search-users")
    @SendToUser("/topic/found-users")
    public List<UserAccount> searchUsers(String s){
        return accountService.searchForUser(s);
    }

    @MessageMapping("/open-chat")
    @SendToUser("/topic/chat")
    public List<Message> openChat(long id){
        return msgService.getChatMessages(id);
    }

    @MessageMapping("/send")
    @RequestMapping("/queue/receive-message")
    public void sendMessage(@Payload Message message){
        long senderId = accountService.getLoggedInUserAccount().getUserId();
        message.setUserId(senderId);
        msgService.saveMessage(message);
        msgService.sendMessage(message);
    }


}

package com.valentine.demo.api;

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

    //live updating search results
    @MessageMapping("/search-users")
    @SendToUser("/topic/found-users")
    public List<UserAccount> searchUsers(String s){
        return accountService.searchForUser(s);
    }

    @MessageMapping("/open-chat")
    @SendToUser("/topic/chat")
    public Chat openChat(long chatId){
        Chat openChat = userChatService.getChatById(chatId);
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
//        DemoApplication.logger.debug("Other User: "+accountService.getUserById(otherUser).getUserName());
        int newMessages = msgService.getAllNewMessages(otherUser).size();
        sendingOperations.convertAndSendToUser(accountService.getUserById(otherUser).getUserName(), "/queue/message-new", newMessages);
    }

    @RequestMapping("/queue/read-message")
    public void readMessage(long chatId){
        msgService.readMessages(chatId);
        UserAccount user = accountService.getLoggedInUserAccount();
        long userId = user.getUserId();
        int newMessages = msgService.getAllNewMessages(userId).size();

        sendingOperations.convertAndSendToUser(user.getUserName(),
                "/queue/read-message",
                newMessages);
    }

}

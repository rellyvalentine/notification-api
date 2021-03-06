package com.valentine.demo.controllers;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dto.UserChatDTO;
import com.valentine.demo.dto.UserPfpDTO;
import com.valentine.demo.entities.Notification;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.entities.messaging.Chat;
import com.valentine.demo.entities.messaging.Message;
import com.valentine.demo.services.NotificationService;
import com.valentine.demo.services.UserAccountService;
import com.valentine.demo.services.messaging.MessagingService;
import com.valentine.demo.services.messaging.UserChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserAccountService accountService;

    @Autowired
    UserChatService chatService;

    @Autowired
    MessagingService msgService;

    @GetMapping("/home")
    public String userHome(Model model){
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> newNotifications = notificationService.getNewNotifications(user.getUserId());
        List<Message> newMessages = msgService.getAllNewMessages(user.getUserId());

        model.addAttribute("user", user);
        model.addAttribute("newNotifications", newNotifications);
        model.addAttribute("newMessages", newMessages);

        return "/user-home";
    }

    @GetMapping("/notifications")
    public String userNotifications(Model model){
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> notifications = notificationService.getAllNotificationsByUserId(user.getUserId());
        List<Notification> newNotifications = notificationService.getNewNotifications(user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("newNotifications", newNotifications);
        return "/notifications";
    }

    @GetMapping("/messages")
    public String userMessages(Model model) {
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> newNotifications = notificationService.getNewNotifications(user.getUserId());
        List<Chat> chats = chatService.getChatsByUserId(user.getUserId());
        List<Message> newMessages = msgService.getAllNewMessages(user.getUserId());
        UserChatDTO addUsers = new UserChatDTO();

        model.addAttribute("msgService", msgService);

        model.addAttribute("addUsers", addUsers);
        model.addAttribute("chats", chats);
        model.addAttribute("user", user);
        model.addAttribute("newNotifications", newNotifications);
        model.addAttribute("newMessages", newMessages);

        return "/chat";
    }

    @PostMapping("/messages/new")
    public String createChat(UserChatDTO addedUsers){

        List<Long> users = addedUsers.getUsers();
        UserAccount user = accountService.getLoggedInUserAccount();
        users.add(user.getUserId()); //add the current user to the chat

        DemoApplication.logger.debug("Users being added: "+users);
        chatService.addUsers(users); //add the users to the chat
        return "redirect:/messages";

    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> newNotifications = notificationService.getNewNotifications(user.getUserId());
        UserPfpDTO updateUser = new UserPfpDTO();
        model.addAttribute("user", user);
        model.addAttribute("updateUser", updateUser);
        model.addAttribute("newnotifications", newNotifications);
        return "/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(UserPfpDTO userPfpDTO) throws IOException {

        UserAccount user = accountService.getLoggedInUserAccount();

        if(!userPfpDTO.getUserName().isEmpty()) {
            user.setName(userPfpDTO.getUserName());
        }

        //handles the image update
        MultipartFile file = userPfpDTO.getFile();

        long pictureNumber = user.getUserId();
        String fileName = "profile-pictures/img"+pictureNumber+".png";
        File destination = new File("src/main/resources/static/"+fileName);

        if(!file.isEmpty()){
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            ImageIO.write(src, "png", destination);
        }

        user.setPfp(fileName);
        accountService.updateUser(user);

        return "redirect:/profile";
    }


}

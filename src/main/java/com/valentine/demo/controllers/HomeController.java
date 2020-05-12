package com.valentine.demo.controllers;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.entities.Notification;
import com.valentine.demo.entities.Person;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.services.NotificationService;
import com.valentine.demo.services.PersonService;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    PersonService personService;

    @Autowired
    UserAccountService accountService;



//    @GetMapping("/")
//    public String showHome(Model model){
//        List<Notification> notifications = notificationService.getNewNotifications();
//        DemoApplication.logger.debug("Notification size: "+notifications.size());
//        model.addAttribute("notifications", notifications);
//        return "/home";
//    }

//    //this is for un-registered users
//    @GetMapping("/all-notifications")
//    public String showAllNotifications(Model model){
//        List<Notification> notifications = notificationService.getAllNotifications();
//        model.addAttribute("notifications", notifications);
//        //if a notification is new:
//            //indicated with blue dot
//            //when clicked, it will show a modal with info
//            //when clicked, it will no longer be new
//        return "/notifications";
//    }

    @GetMapping("/home")
    public String userHome(Model model){
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> notifications = notificationService.getAllNotificationsByUserId(user.getUserId());
        model.addAttribute("notifications", notifications);

        return "/user-home";
    }



}

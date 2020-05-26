package com.valentine.demo.controllers;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dto.UserPfpDTO;
import com.valentine.demo.entities.Notification;
import com.valentine.demo.entities.Person;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.image.ProfilePicture;
import com.valentine.demo.image.ProfilePictureService;
import com.valentine.demo.services.NotificationService;
import com.valentine.demo.services.PersonService;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
import java.util.Random;

@Controller
public class HomeController {

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserAccountService accountService;

//    @Autowired
//    ProfilePictureService pfpService;

    @GetMapping("/home")
    public String userHome(Model model){
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> notifications = notificationService.getNewNotifications(user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("notifications", notifications);

        return "/user-home";
    }

    @GetMapping("/notifications")
    public String userNotifications(Model model){
        UserAccount user = accountService.getLoggedInUserAccount();
        List<Notification> notifications = notificationService.getAllNotificationsByUserId(user.getUserId());
        model.addAttribute("notifications", notifications);
        return "/notifications";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        UserAccount user = accountService.getLoggedInUserAccount();
        UserPfpDTO updateUser = new UserPfpDTO();
        model.addAttribute("user", user);
        model.addAttribute("updateUser", updateUser);
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

//        ProfilePicture image = new ProfilePicture();
//        image.setId(pictureNumber);
//        pfpService.save(image);
        user.setPfp(fileName);
        accountService.updateUser(user);

        return "redirect:/profile";
    }


}

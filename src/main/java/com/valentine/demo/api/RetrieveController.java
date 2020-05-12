package com.valentine.demo.api;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

//an API that retrieves information from the database

@RestController
public class RetrieveController {

   @Autowired
   private UserAccountService userService;

    @Autowired
    private NotificationService notificationService;


//    @GetMapping("/api-v1/retrieve")
//    public Person getPerson(){
//
//        List<Person> people = personService.getAllPeople();
//        Random rand = new Random(); //get a random index from the list
//        long id = people.get(rand.nextInt(people.size())).getId(); //get the id of the random person
//
//        return personService.getPersonById(id);
//    }

    @GetMapping("/api-v1-retrieve")
    @MessageMapping("/get-person")
    @SendTo("/topic/random-server")
    public String getRandomUser() throws Exception {

        Thread.sleep(1000);

        List<UserAccount> users = userService.getAllUsers();
        Random rand = new Random(); //get a random index from the list
        DemoApplication.logger.debug("User accessed: "+users.get(rand.nextInt(users.size())).getUserName());
        return users.get(rand.nextInt(users.size())).getUserName(); //return the name of the random users
    }


    @GetMapping("/api-v1/notifs")
    public List<Notification> getNotifications(){
        long userId = userService.getLoggedInUserAccount().getUserId();
        return notificationService.getAllNotificationsByUserId(userId);
    }

    @GetMapping("/api-v1/new-notifs")
    public List<Notification> getNewNotifications(){
        long userId = userService.getLoggedInUserAccount().getUserId();
        return notificationService.getNewNotifications(userId);
    }


//we will create our notification here and update the user it goes to through the websocket subscription
//  @MessageMapping("/new-notif")
    @PostMapping("/api-v1/save-notif")
    @SendTo("/topic/bell")
    public int addNotification(long userId) throws Exception {
        Thread.sleep(1000);
        DemoApplication.logger.debug("This notification is for: "+userId);

        Notification notification = new Notification();

//        LocalDate current = LocalDate.now(); //gives us the current date WITHOUT TIME
        ZonedDateTime current = ZonedDateTime.now(ZoneId.of("America/New_York")); //gives us the current date from time zone

        //format the current time for the sql timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String currentDateString = current.format(formatter);
//        DemoApplication.logger.debug(currentDateString); //for testing

        Timestamp sqlDate = Timestamp.valueOf(currentDateString); //convert to sql

        notification.setDate(sqlDate); //set the notification to the current time in SQL
        notification.setUserId(userId);
        notification.setHead("Random Access");
        notification.setBody("You have been accessed by a random user");
        notification.setNew(true);
        notificationService.saveNotification(notification);

        //return the notifications for the specific user
        return notificationService.getAllNotificationsByUserId(userId).size();
    }

    @PostMapping("/api-v1/read-notif")
    public void readNotification(@RequestBody long notificationId){
//        DemoApplication.logger.debug("Reading notification: "+notificationId);
        //the notification is no longer new since it's been opened
        notificationService.readNotification(notificationId);
    }


}

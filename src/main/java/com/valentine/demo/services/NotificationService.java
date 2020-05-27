package com.valentine.demo.services;

import com.valentine.demo.dao.NotificationStore;
import com.valentine.demo.entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationStore notificationStore;

    public List<Notification> getAllNotificationsByUserId(long userId){
        return notificationStore.findAllByUserId(userId);
    }

    public List<Notification> getNewNotifications(long userId){
        List<Notification> all = notificationStore.findAllByUserId(userId);

        //returning the new notifications
        return  all.stream()
                .filter(notif -> notif.isNew()) //filter - excludes false values for this condition
                .collect(Collectors.toList()); //if a notification is new it will be collected
    }

    public void createNewNotification(long userId){

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
        saveNotification(notification);
    }

    private void saveNotification(Notification notification){
        notificationStore.save(notification);
    }

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void readNotification(long notificationId){
        entityManager.createNativeQuery("UPDATE notification SET is_new = false WHERE id = ?;")
                .setParameter(1, notificationId)
                .executeUpdate();
    }

}

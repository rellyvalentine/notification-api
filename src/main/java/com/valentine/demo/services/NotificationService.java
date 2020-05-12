package com.valentine.demo.services;

import com.valentine.demo.dao.NotificationStore;
import com.valentine.demo.entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public void saveNotification(Notification notification){
        notificationStore.save(notification);
    }

//    public Notification getNotificationById(long id){
//        return notificationStore.findById(id);
//    }

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void readNotification(long notificationId){
        entityManager.createNativeQuery("UPDATE notification SET is_new = false WHERE id = ?;")
                .setParameter(1, notificationId)
                .executeUpdate();
    }

}

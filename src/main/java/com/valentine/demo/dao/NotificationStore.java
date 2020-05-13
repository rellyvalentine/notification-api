package com.valentine.demo.dao;

import com.valentine.demo.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationStore extends JpaRepository<Notification, Long> {

    @Override
    @Query(nativeQuery = true, value = "SELECT id, head, body, date, is_new FROM notification ORDER BY date DESC")
    List<Notification> findAll();

    @Query(nativeQuery = true, value = "SELECT id, head, body, date, is_new, user_id " +
            "FROM notification " +
            "WHERE user_id = ?1 ORDER BY date DESC")
    List<Notification> findAllByUserId(@Param("user_id") long user_id);

//    @Query(nativeQuery = true, value = "SELECT id, head, body, date, is_new " +
//            "FROM notification" +
//            " WHERE id = ?1")
//    Notification findById(@Param("id") long id);

}

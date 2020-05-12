package com.valentine.demo.dao.messaging;

import com.valentine.demo.entities.messaging.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageStore extends JpaRepository<Message, Long> {



}

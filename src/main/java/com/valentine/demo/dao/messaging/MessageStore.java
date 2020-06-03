package com.valentine.demo.dao.messaging;

import com.valentine.demo.entities.messaging.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageStore extends JpaRepository<Message, Long> {

    @Query(nativeQuery = true, value = "SELECT message_id, user_id, chat_id, date, is_new " +
            "FROM message " +
            "WHERE chat_id = ?1")
    public List<Message> getMessagesByChatId(long chatId);

}

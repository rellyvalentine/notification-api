package com.valentine.demo.dao.messaging;

import com.valentine.demo.entities.messaging.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(nativeQuery = true, value = "SELECT c.chat_id, c.topic, c.password " +
            "FROM chat c " +
            "INNER JOIN user_chat uc " +
            "ON c.chat_id = uc.chat_id " +
            "WHERE uc.user_id = ?1")
    public List<Chat> getChatsByUserId(long userId);

}

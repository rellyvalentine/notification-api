package com.valentine.demo.dao;

import com.valentine.demo.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Override
    List<UserAccount> findAll();

    Optional<UserAccount> findUserAccountByUserName(String s);

    @Query(nativeQuery = true, value = "SELECT user_id, username, password, role " +
            "FROM user_accounts " +
            "WHERE username = ?1")
    UserAccount getUserAccountByUserName(String username);

    @Query(nativeQuery = true, value = "SELECT user_id, username, password, role " +
            "FROM user_accounts " +
            "WHERE user_id = ?1")
    UserAccount getUserAccountById(long id);


}

package com.valentine.demo.services;

import com.valentine.demo.DemoApplication;
import com.valentine.demo.dao.UserAccountRepository;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.security.UserAccountDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserAccountService implements UserDetailsService {

    @Autowired
    UserAccountRepository userAccountRepo;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAccount> user = userAccountRepo.findUserAccountByUserName(username);

        user.orElseThrow(() -> new UsernameNotFoundException("user Not Found: "+username));

        return user.map(UserAccountDetails::new).get();
    }

    public UserAccount getLoggedInUserAccount() throws ClassCastException{
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        username = ((UserDetails)principal).getUsername();
        return userAccountRepo.getUserAccountByUserName(username);
    }

    public void saveUser(UserAccount userAccount){
        userAccount.setPfp("/default-images/default-profile.png");
        userAccountRepo.save(userAccount);
    }

    public UserAccount getUserById(long id){
        return userAccountRepo.getUserAccountById(id);
    }

    public List<UserAccount> getAllUsers(){
        return userAccountRepo.findAll();
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    public UserAccount getRandomUser() {
        List<UserAccount> users = getAllUsers();
        Random rand = new Random(); //get a random index from the list
        UserAccount user = users.get(rand.nextInt(users.size()));
        DemoApplication.logger.debug("User accessed: "+user);
        return user;
    }

    @Transactional
    public void updateUser(UserAccount user) {
        entityManager.createNativeQuery("UPDATE user_accounts " +
                "SET name = ?, pfp = ?" +
                "WHERE user_id = ?;")
                .setParameter(1, user.getName())
                .setParameter(2, user.getPfp())
                .setParameter(3, user.getUserId())
                .executeUpdate();
    }

    /*
    returns a list of users that contain the search value within their username or their name
    similar to Twitter's searching algorithm
     */
    public List<UserAccount> searchForUser(String s){
        List<UserAccount> allUsers = userAccountRepo.findAll();
//        allUsers.sort(Comparator.comparing(UserAccount::getUserName)); //alphabetically sort the users by username
         return allUsers.stream()
                .filter(user -> containsValue(user.getUserName(), user.getName(), s))
                .collect(Collectors.toList());
    }

    private boolean containsValue(String username, String name, String value){
        return username.contains(value) || name.contains(value);
    }

}


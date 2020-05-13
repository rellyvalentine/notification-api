package com.valentine.demo.services;

import com.valentine.demo.dao.UserAccountRepository;
import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.security.UserAccountDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserAccountService implements UserDetailsService {

    @Autowired
    UserAccountRepository userAccountRepo;

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
        userAccountRepo.save(userAccount);
    }

    public UserAccount getUserById(long id){
        return userAccountRepo.getUserAccountById(id);
    }

    public List<UserAccount> getAllUsers(){
        return userAccountRepo.findAll();
    }
}


package com.valentine.demo.security;

import com.valentine.demo.entities.UserAccount;
import com.valentine.demo.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistryController {

    @Autowired
    BCryptPasswordEncoder bCryptEncoder;

    @Autowired
    UserAccountService userAccountService;

    @GetMapping("/register")
    public String register(Model model){

        UserAccount user = new UserAccount();
        model.addAttribute("userAccount", user);

        return "/registration";
    }

    @PostMapping("/register/save")
    public String saveUser(Model model, UserAccount user){
        user.setPassword(bCryptEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userAccountService.saveUser(user);
        return "redirect:/home";
    }

}

package com.valentine.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Autowired
    BCryptPasswordEncoder bCryptEncoder;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new UrlAuthenticationSuccessHandler();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .usersByUsernameQuery("SELECT username, password, user_id FROM user_accounts WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, role FROM user_accounts WHERE username = ?")
                .dataSource(dataSource)
                .passwordEncoder(bCryptEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .ignoringAntMatchers("/topic/**", "/api-v1/**")
                .and()
                .headers()
                .frameOptions().sameOrigin()
                .and()
                .authorizeRequests();

        http.authorizeRequests()
                .antMatchers("/messages").hasAuthority("USER")
                .antMatchers("/home").hasAuthority("USER")
                .antMatchers("/notifications").hasAuthority("USER")
                .antMatchers("/api-v1/read-notif").hasAuthority("USER")
                .antMatchers("/","/**").permitAll()
                .and()
                .formLogin()
                .successHandler(authenticationSuccessHandler());

//        http.csrf().disable(); //temporary - Spring automatically disables POST methods without csrf tokens
    }


}

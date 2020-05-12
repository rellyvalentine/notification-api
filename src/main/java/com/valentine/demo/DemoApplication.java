package com.valentine.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static final Logger logger = LoggerFactory.getLogger("com.valentine");

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

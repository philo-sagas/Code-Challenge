package com.philo.challenge.pong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PongApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(PongApplication.class, args);
    }

    @Autowired
    private PongConstants pongConstants;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("os.name = {}", System.getProperty("os.name"));
        log.info("ServerName = {}", System.getProperty("ServerName"));

        log.info("pong.throttling = {}", pongConstants.getThrottling());
    }
}

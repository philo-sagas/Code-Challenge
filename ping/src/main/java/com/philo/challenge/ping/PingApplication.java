package com.philo.challenge.ping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.Disposable;

@Slf4j
@SpringBootApplication
public class PingApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(PingApplication.class, args);
    }

    @Autowired
    private PingConstants pingConstants;

    @Autowired
    private PingController pingController;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("os.name = {}", System.getProperty("os.name"));
        log.info("ServerName = {}", System.getProperty("ServerName"));

        log.info("ping.enabled-send = {}", pingConstants.isEnabledSend());
        log.info("ping.rate-limit = {}", pingConstants.getRateLimit());
        log.info("ping.pong-base-url = {}", pingConstants.getPongBaseUrl());
        log.info("ping.file-lock-path = {}", pingConstants.getFileLockPath());

        if (pingConstants.isEnabledSend()) {
            Disposable disposable = pingController.preform();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> disposable.dispose()));
        }
    }
}

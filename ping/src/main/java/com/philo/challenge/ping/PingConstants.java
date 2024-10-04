package com.philo.challenge.ping;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ping")
public class PingConstants {
    private boolean enabledSend;

    private int rateLimit;

    private String pongBaseUrl;

    private String fileLockPath;

    private int mockPort;
}

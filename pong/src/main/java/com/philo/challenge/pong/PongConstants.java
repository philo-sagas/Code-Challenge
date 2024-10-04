package com.philo.challenge.pong;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pong")
public class PongConstants {
    private int throttling;
}

package com.philo.challenge.ping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    @Autowired
    private PingConstants pingConstants;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.baseUrl(pingConstants.getPongBaseUrl()).build();
    }
}

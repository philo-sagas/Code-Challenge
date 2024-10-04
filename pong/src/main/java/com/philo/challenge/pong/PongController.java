package com.philo.challenge.pong;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pong")
public class PongController {
    private static final long ONE_SECOND_TO_NANOS = 1000000000L;

    private final AtomicLong lastNanos = new AtomicLong();

    private final AtomicInteger counter = new AtomicInteger();

    @NonNull
    private PongConstants pongConstants;

    @PostMapping
    public Mono<ResponseEntity<String>> pong(@RequestBody Mono<String> messageMono) {
        return messageMono.doOnNext(msg -> log.info("Received message: {}", msg))
                .map(msg -> {
                    long elapsedNanos = System.nanoTime() - lastNanos.get();
                    if (elapsedNanos > ONE_SECOND_TO_NANOS) {
                        lastNanos.set(System.nanoTime());
                        counter.set(1);
                        log.info("Return '200 OK'. (interval: {}s, frequency: {})", Math.round(elapsedNanos / (ONE_SECOND_TO_NANOS / 1000.0)) / 1000.0, counter.get());
                        return ResponseEntity.status(HttpStatus.OK).body("World");
                    } else if (counter.get() < pongConstants.getThrottling()) {
                        counter.incrementAndGet();
                        log.info("Return '200 OK'. (interval: {}s, frequency: {})", Math.round(elapsedNanos / (ONE_SECOND_TO_NANOS / 1000.0)) / 1000.0, counter.get());
                        return ResponseEntity.status(HttpStatus.OK).body("World");
                    } else {
                        log.warn("Return '429 Too Many Requests'. (interval: {}s, frequency: {})", Math.round(elapsedNanos / (ONE_SECOND_TO_NANOS / 1000.0)) / 1000.0, counter.get());
                        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
                    }
                });
    }
}

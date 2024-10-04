package com.philo.challenge.ping;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@Component
public class PingController {
    private static final long ONE_SECOND_TO_NANOS = 1000000000L;

    @NonNull
    private WebClient webClient;

    @NonNull
    private PingConstants pingConstants;

    public Disposable preform() {
        return Flux.interval(Duration.ofMillis(1000L))
                .doOnNext(i -> this.controlRate(this::sayHello)
                        .subscribeOn(Schedulers.parallel())
                        .subscribe(result -> log.info("Received successfully. (result: {})", result),
                                throwable -> log.error("Request failed! (error: {})", throwable.getMessage())))
                .subscribe();
    }

    @SneakyThrows
    public Mono<String> controlRate(Supplier<Mono<String>> supplier) {
        Mono<String> result = Mono.empty();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(pingConstants.getFileLockPath(), "rw")) {
            try (FileChannel fileChannel = randomAccessFile.getChannel()) {
                FileLock fileLock = fileChannel.lock();
                String lastNanosString = randomAccessFile.readLine();
                long lastNanos = NumberUtils.toLong(lastNanosString, 0);
                String counterString = randomAccessFile.readLine();
                int counter = NumberUtils.toInt(counterString, 0);
                long elapsedNanos = System.nanoTime() - lastNanos;
                if (elapsedNanos > ONE_SECOND_TO_NANOS) {
                    lastNanos = System.nanoTime();
                    counter = 1;
                    result = supplier.get();
                    log.info("Request sent. (interval: {}s, frequency: {})", Math.round(elapsedNanos / (ONE_SECOND_TO_NANOS / 1000.0)) / 1000.0, counter);
                } else if (counter < pingConstants.getRateLimit()) {
                    ++counter;
                    result = supplier.get();
                    log.info("Request sent. (interval: {}s, frequency: {})", Math.round(elapsedNanos / (ONE_SECOND_TO_NANOS / 1000.0)) / 1000.0, counter);
                } else {
                    log.warn("Request not send because rate limited. (interval: {}s, frequency: {})", Math.round(elapsedNanos / (ONE_SECOND_TO_NANOS / 1000.0)) / 1000.0, counter);
                }
                randomAccessFile.seek(0);
                randomAccessFile.writeBytes(lastNanos + "\n");
                randomAccessFile.writeBytes(counter + "\n");
                fileLock.release();
            }
        }
        return result;
    }

    Mono<String> sayHello() {
        return webClient.post().uri("/pong")
                .body(Mono.just("Hello"), String.class)
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(String.class);
                    } else {
                        return clientResponse.createError();
                    }
                });
    }
}

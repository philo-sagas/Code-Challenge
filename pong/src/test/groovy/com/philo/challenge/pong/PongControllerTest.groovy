package com.philo.challenge.pong

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PongControllerTest extends Specification {
    @Autowired
    WebTestClient webTestClient

    def "test post url '/pong'"() {
        when:
        TimeUnit.MILLISECONDS.sleep(1000L)
        def response = webTestClient.post().uri("/pong")
                .body(Mono.just("Hello"), String.class)
                .exchange()
        TimeUnit.MILLISECONDS.sleep(500L)
        def response2 = webTestClient.post().uri("/pong")
                .body(Mono.just("Hello"), String.class)
                .exchange()
        def response3 = webTestClient.post().uri("/pong")
                .body(Mono.just("Hello"), String.class)
                .exchange()

        then:
        response.expectStatus().isOk().expectBody(String.class).isEqualTo("World")
        response2.expectStatus().isOk().expectBody(String.class).isEqualTo("World")
        response3.expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }

}

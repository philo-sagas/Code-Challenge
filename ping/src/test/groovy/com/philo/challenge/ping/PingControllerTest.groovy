package com.philo.challenge.ping

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PingControllerTest extends Specification {

    @Autowired
    PingController pingController

    @Autowired
    PingConstants pingConstants

    def mockWebServer = new MockWebServer()

    def setup() {
        mockWebServer.start(pingConstants.getMockPort())
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.OK.value()).setBody("World"))
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.TOO_MANY_REQUESTS.value()))
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    def "test preform"() {
        expect:
        def disposable = pingController.preform()
        TimeUnit.SECONDS.sleep(4)
        disposable.dispose()
        TimeUnit.SECONDS.sleep(2)
    }

    def "test sayHello"() {
        when:
        def resultMono = pingController.sayHello()
        def resultMono2 = pingController.sayHello()

        then:
        StepVerifier.create(resultMono).expectNext("World").verifyComplete()
        StepVerifier.create(resultMono2).expectError(WebClientResponseException.TooManyRequests.class).verify()
    }

    def "test controlRate"() {
        given:
        def supplier = () -> Mono.just("World")

        when:
        TimeUnit.MILLISECONDS.sleep(delayMillis)
        def resultMono = pingController.controlRate(supplier)

        then:
        if (result != null)
            StepVerifier.create(resultMono).expectNext(result).verifyComplete()
        else
            StepVerifier.create(resultMono).verifyComplete()

        where:
        delayMillis || result
        1000L       || "World"
        500L        || "World"
        0L          || null
    }
}

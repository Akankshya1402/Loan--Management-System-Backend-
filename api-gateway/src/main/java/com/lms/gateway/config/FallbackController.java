package com.lms.gateway.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/fallback/customer")
    public Mono<String> customerFallback() {
        return Mono.just("Customer Service is temporarily unavailable");
    }
}

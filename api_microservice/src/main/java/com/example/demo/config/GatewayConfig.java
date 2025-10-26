package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_microservice", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/auth/(?<segment>.*)", "/auth/${segment}")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Expose-Headers", "RETAIN_FIRST"))
                        .uri("http://localhost:8083"))

                // USERS
                .route("user_microservice", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                // forward exactly as-is, no rewrite
                                .stripPrefix(1)
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Expose-Headers", "RETAIN_FIRST"))
                        .uri("http://localhost:8081"))

                .route("device_microservice", r -> r
                        .path("/api/device/**")
                        .filters(f -> f
                                .stripPrefix(1)
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Expose-Headers", "RETAIN_FIRST"))
                        .uri("http://localhost:8082"))
                .build();
    }
}

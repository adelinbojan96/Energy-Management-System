package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@SpringBootApplication
@EnableWebFluxSecurity
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() 
                        .pathMatchers("/chat/**", "/ws/**", "/ws/info/**", "/ws").permitAll() 
                        .pathMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() 
                        .anyExchange().permitAll() 
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8088"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_microservice", r -> r.path("/api/auth/**")
                        .filters(f -> f.rewritePath("/api/auth/(?<segment>.*)", "/auth/${segment}")).uri("http://auth-service:8083"))
                .route("user_microservice", r -> r.path("/api/users/**").filters(f -> f.stripPrefix(1)).uri("http://user-service:8081"))
                .route("device_microservice", r -> r.path("/api/device/**").filters(f -> f.stripPrefix(1)).uri("http://device-service:8082"))
                .route("monitoring_microservice", r -> r.path("/api/monitoring/**")
                        .uri("http://load-balancer-service:8090"))

                .route("chat_rest", r -> r.path("/chat/**").uri("http://chat-service:8089"))
                
                .route("chat_ws_upgrade", r -> r.path("/ws/**").and().header("Upgrade", "websocket").uri("ws://chat-service:8089"))
                
                .route("chat_http_fallback", r -> r.path("/ws/**").uri("http://chat-service:8089"))

                .build();
    }
}
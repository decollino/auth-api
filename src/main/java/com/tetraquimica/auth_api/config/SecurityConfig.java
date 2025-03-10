package com.tetraquimica.auth_api.config;

import com.tetraquimica.auth_api.security.JWTAuthenticationWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         JWTAuthenticationWebFilter jwtAuthenticationWebFilter) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        // Permite endpoints de autenticação sem token
                        .pathMatchers("/auth/**").permitAll()
                        // Exige ROLE_ADMIN para GET /usuarios/**
                        .pathMatchers(HttpMethod.GET, "/usuarios/**").hasRole("ADMIN")
                        // Demais endpoints liberados
                        .anyExchange().permitAll()
                )
                // Adiciona seu filtro de JWT na posição de AUTHENTICATION
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}

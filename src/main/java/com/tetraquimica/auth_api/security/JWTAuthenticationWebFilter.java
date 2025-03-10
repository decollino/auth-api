package com.tetraquimica.auth_api.security;

import com.tetraquimica.auth_api.services.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

@Component
public class JWTAuthenticationWebFilter implements WebFilter {

    private final JWTService jwtService;

    public JWTAuthenticationWebFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Lê o header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Se não existe ou não começa com Bearer, apenas segue o fluxo (sem autenticar)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {
            // Extrai as claims do token
            Claims claims = jwtService.extractClaim(token, c -> c);

            // Verifica se o token não está expirado
            if (!jwtService.isTokenExpired(token)) {
                // Monta a Authentication com a role
                Authentication auth = jwtService.getAuthentication(claims);

                // Injeta a autenticação no contexto reativo
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            } else {
                // Token expirado
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

        } catch (ExpiredJwtException | SignatureException e) {
            // Token inválido ou assinatura incorreta
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}

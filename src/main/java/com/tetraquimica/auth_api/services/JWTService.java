package com.tetraquimica.auth_api.services;

import com.tetraquimica.auth_api.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime; // Tempo de expiração do token

    // 🔹 Obtém a chave secreta para assinar o token
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🔹 Gera um token JWT com o e-mail e role do usuário
    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail()) // Define o email como "sub"
                .claim("role", "ROLE_" + usuario.getRole()) // Define o papel do usuário com "ROLE_"
                .setIssuedAt(new Date()) // Data de criação
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Data de expiração
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Assina com a chave secreta
                .compact();
    }

    // 🔹 Valida se o token é válido e não expirou
    public boolean validateToken(String token, Usuario usuario) {
        String email = extractEmail(token);
        return (email.equals(usuario.getEmail()) && !isTokenExpired(token));
    }

    // 🔹 Extrai o e-mail (subject) do token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 🔹 Verifica se o token já expirou
    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // 🔹 Extrai um dado específico do token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // Usa a chave para validar
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // 🔹 Obtém a autenticação a partir dos claims do JWT
    public Authentication getAuthentication(Claims claims) {
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        // Garantindo que o role tenha o prefixo correto
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(role)) // Adiciona a role do usuário
        );
    }

}

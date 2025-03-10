package com.tetraquimica.auth_api.controllers;

import com.tetraquimica.auth_api.models.Usuario;
import com.tetraquimica.auth_api.services.AuthService;
import com.tetraquimica.auth_api.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Usuario usuario) {
        System.out.println("/auth/login inicio controller");
        return authService.authenticate(usuario.getEmail(), usuario.getSenha())
                .map(authenticatedUser -> {
                    // Gera o token JWT para o usuário autenticado
                    String token = jwtService.generateToken(authenticatedUser);
                    Map<String, String> response = new HashMap<>();
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Usuário ou senha inválidos!"))));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Em aplicações reativas, o logout ainda pode ser apenas um endpoint simples
        return ResponseEntity.ok("Logout realizado com sucesso.");
    }
}

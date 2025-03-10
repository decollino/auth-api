package com.tetraquimica.auth_api.services;

import com.tetraquimica.auth_api.models.Usuario;
import com.tetraquimica.auth_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Para verificar senha hashada

    @Autowired
    private JWTService jwtService; // Ainda vamos criar esse serviço

    public Mono<String> autenticar(String email, String senha) {
        return usuarioRepository.findByEmail(email)
                .flatMap(usuario -> {
                    // Verifica se a senha informada é igual à senha armazenada (hash)
                    if (passwordEncoder.matches(senha, usuario.getSenha())) {
                        // Gera um token JWT (vamos criar o JWTService no próximo passo)
                        String token = jwtService.generateToken(usuario);
                        return Mono.just(token);
                    } else {
                        return Mono.error(new RuntimeException("Senha incorreta!"));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Usuário não encontrado!")));
    }

    public Mono<Usuario> authenticate(String email, String senha) {
        return usuarioRepository.findByEmail(email)
                .filter(usuario -> passwordEncoder.matches(senha, usuario.getSenha()));
    }
}

package com.tetraquimica.auth_api.services;

import com.tetraquimica.auth_api.models.Usuario;
import com.tetraquimica.auth_api.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Flux<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Mono<Usuario> criarUsuario(Usuario usuario) {
        // Garante que o ID seja gerado caso não venha no request
        if (usuario.getId() == null || usuario.getId().isEmpty()) {
            usuario.setId(UUID.randomUUID().toString());
        }

        // Log para verificar se o ID está sendo enviado corretamente
        logger.info("Criando usuário: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        // Hash da senha antes de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Salva no Firestore
        return usuarioRepository.save(usuario);
    }

    public Mono<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}

package com.tetraquimica.auth_api.repositories;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.tetraquimica.auth_api.models.Usuario;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UsuarioRepository extends FirestoreReactiveRepository<Usuario> {
    Mono<Usuario> findByEmail(String email);  // 🔹 Método para buscar usuário por email
}

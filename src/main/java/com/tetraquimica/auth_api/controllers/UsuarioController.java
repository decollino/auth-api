package com.tetraquimica.auth_api.controllers;

import com.tetraquimica.auth_api.models.Usuario;
import com.tetraquimica.auth_api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public Flux<Usuario> listarTodos() {
        return usuarioService.listarTodos();
    }

    @PostMapping
    public Mono<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.criarUsuario(usuario);
    }

    @GetMapping("/email/{email}")
    public Mono<Usuario> buscarPorEmail(@PathVariable String email) {
        return usuarioService.buscarPorEmail(email);
    }
}

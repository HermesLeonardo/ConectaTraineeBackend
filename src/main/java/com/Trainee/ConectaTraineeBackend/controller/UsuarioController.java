package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")  // Definindo a rota usuarios
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        logger.info("Listando todos os usuários.");
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/desativados")
    public ResponseEntity<List<Usuario>> listarDesativados() {
        logger.info("Listando usuários desativados.");
        List<Usuario> usuarios = usuarioService.listarDesativados();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        logger.info("Recebida requisição para criar usuário com email: {}", usuario.getEmail());
        Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
        logger.info("Usuário criado com sucesso: {}", novoUsuario.getId());
        return ResponseEntity.ok(novoUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando usuário com ID: {}", id);
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok)
                      .orElseGet(() -> {
                          logger.warn("Usuário com ID {} não encontrado.", id);
                          return ResponseEntity.notFound().build();
                      });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        logger.info("Deletando usuário com ID: {}", id);
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/atividades")
public ResponseEntity<Set<Atividade>> listarAtividades(@PathVariable Long id) {
    return ResponseEntity.ok(usuarioService.listarAtividades(id));
}

    @PutMapping("/{id}/desativar")
    public ResponseEntity<String> desativarUsuario(@PathVariable Long id) {
        usuarioService.desativarUsuario(id);
        return ResponseEntity.ok("Usuário desativado com sucesso!");
    }


    @GetMapping("/{id}/tem-vinculacoes")
    public ResponseEntity<Boolean> verificarVinculacoes(@PathVariable Long id) {
        boolean temVinculacoes = usuarioService.temVinculacoes(id);
        return ResponseEntity.ok(temVinculacoes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        logger.info("Requisição recebida para atualizar usuário com ID: {}", id);

        // verifica existência do usuário antes de atualizar
        Optional<Usuario> usuarioExistente = usuarioService.buscarPorId(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuarioAtualizar = usuarioExistente.get();

            // Atualize os campos necessários
            usuarioAtualizar.setNome(usuario.getNome());
            usuarioAtualizar.setEmail(usuario.getEmail());
            usuarioAtualizar.setPerfil(usuario.getPerfil());
            usuarioAtualizar.setAtivo(usuario.isAtivo());

            // Se senha for enviada, atualize também
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                usuarioAtualizar.setSenha(usuario.getSenha());
            }

            Usuario usuarioSalvo = usuarioService.salvarUsuario(usuarioAtualizar);
            logger.info("✅ Usuário com ID: {} atualizado com sucesso.", usuarioSalvo.getId());

            return ResponseEntity.ok(usuarioSalvo);
        } else {
            logger.warn("❌ Usuário com ID: {} não encontrado para atualização.", id);
            return ResponseEntity.notFound().build();
        }
    }


}

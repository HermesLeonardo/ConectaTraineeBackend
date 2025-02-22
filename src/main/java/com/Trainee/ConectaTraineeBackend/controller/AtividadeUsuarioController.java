package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Trainee.ConectaTraineeBackend.service.ProjetoAtividadeService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/atividades-usuarios")  // Alteração aqui
public class AtividadeUsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(AtividadeUsuarioController.class);

    @Autowired
    private AtividadeService atividadeService;

   
    @PostMapping("/{id}/usuarios")
    public ResponseEntity<Atividade> adicionarUsuarios(@PathVariable Long id, @RequestBody Set<Usuario> usuarios) {
        logger.info("Adicionando {} usuários à atividade ID {}", usuarios.size(), id);
        return ResponseEntity.ok(atividadeService.adicionarUsuarios(id, usuarios));
    }

   
    @DeleteMapping("/{id}/usuarios/{idUsuario}")
    public ResponseEntity<Atividade> removerUsuario(@PathVariable Long id, @PathVariable Long idUsuario) {
        logger.info("Removendo usuário ID {} da atividade ID {}", idUsuario, id);
        return ResponseEntity.ok(atividadeService.removerUsuario(id, idUsuario));
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<Set<Usuario>> listarUsuarios(@PathVariable Long id) {
        logger.info("Listando usuários responsáveis pela atividade ID {}", id);
        return ResponseEntity.ok(atividadeService.listarUsuarios(id));
    }
}

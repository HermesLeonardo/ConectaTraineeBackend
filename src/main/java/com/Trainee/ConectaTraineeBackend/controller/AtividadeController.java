package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/atividades")
public class AtividadeController {

    private static final Logger logger = LoggerFactory.getLogger(AtividadeController.class);

    @Autowired
    private AtividadeService atividadeService;

    @GetMapping
    public ResponseEntity<List<Atividade>> listarTodos() {
        logger.info("Listando todas as atividades.");
        return ResponseEntity.ok(atividadeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando atividade com ID: {}", id);
        Optional<Atividade> atividade = atividadeService.buscarPorId(id);
        return atividade.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Atividade com ID {} não encontrada.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<Atividade> criarAtividade(@RequestBody Atividade atividade) {
        logger.info("Criando atividade: {}", atividade.getNome());
        return ResponseEntity.ok(atividadeService.salvarAtividade(atividade));
    }

    @PutMapping("/{id}/usuarios")
    public ResponseEntity<Atividade> adicionarUsuarios(@PathVariable Long id, @RequestBody Set<Usuario> usuarios) {
        logger.info("Adicionando usuários à atividade {}", id);
        return ResponseEntity.ok(atividadeService.adicionarUsuarios(id, usuarios));
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<Set<Usuario>> listarUsuarios(@PathVariable Long id) {
        logger.info("Listando usuários da atividade {}", id);
        return ResponseEntity.ok(atividadeService.listarUsuarios(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtividade(@PathVariable Long id) {
        logger.info("Deletando atividade com ID: {}", id);
        atividadeService.deletarAtividade(id);
        return ResponseEntity.noContent().build();
    }
}

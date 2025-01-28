package com.Trainee.ConectaTraineeBackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.Trainee.ConectaTraineeBackend.service.ProjetosService;
import com.Trainee.ConectaTraineeBackend.model.Projetos;

@RestController
@RequestMapping("/api/projetos") // Definindo a rota para projetos
public class ProjetosController {

    private static final Logger logger = LoggerFactory.getLogger(ProjetosController.class);

    @Autowired
    private ProjetosService projetosService;

    @GetMapping
    public ResponseEntity<List<Projetos>> listarTodos() {
        logger.info("Listando todos os projetos.");
        List<Projetos> projetos = projetosService.listarTodos();
        return ResponseEntity.ok(projetos);
    }

    @PostMapping
    public ResponseEntity<Projetos> criarProjeto(@RequestBody Projetos projetos) {
        logger.info("Criando novo projeto: {}", projetos.getNome());
        Projetos novoProjeto = projetosService.salvarProjeto(projetos);
        logger.info("Projeto criado com sucesso: ID {}", novoProjeto.getId());
        return ResponseEntity.ok(novoProjeto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projetos> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando projeto com ID: {}", id);
        Optional<Projetos> projeto = projetosService.buscarPorId(id);
        return projeto.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Projeto com ID {} não encontrado.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetosService.deletarProjeto(id);
        return ResponseEntity.noContent().build();
    }
}

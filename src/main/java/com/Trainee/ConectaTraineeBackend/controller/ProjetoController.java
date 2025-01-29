package com.Trainee.ConectaTraineeBackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.Trainee.ConectaTraineeBackend.service.ProjetoService;
import com.Trainee.ConectaTraineeBackend.model.Projeto;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private static final Logger logger = LoggerFactory.getLogger(ProjetoController.class);

    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<Projeto>> listarTodos() {
        logger.info("Listando todos os projetos.");
        List<Projeto> projetos = projetoService.listarTodos();
        return ResponseEntity.ok(projetos);
    }

    @PostMapping
    public ResponseEntity<Projeto> criarProjeto(@RequestBody Projeto projeto) {
        logger.info("Criando novo projeto: {}", projeto.getNome());
        Projeto novoProjeto = projetoService.salvarProjeto(projeto);
        logger.info("Projeto criado com sucesso: ID {}", novoProjeto.getId());
        return ResponseEntity.ok(novoProjeto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando projeto com ID: {}", id);
        Optional<Projeto> projeto = projetoService.buscarPorId(id);
        return projeto.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Projeto com ID {} não encontrado.", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetoService.deletarProjeto(id);
        return ResponseEntity.noContent().build();
    }
}

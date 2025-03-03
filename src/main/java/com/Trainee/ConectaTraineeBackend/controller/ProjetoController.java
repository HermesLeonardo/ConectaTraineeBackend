package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.DTO.ProjetoRequest;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoUsuarioRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.Trainee.ConectaTraineeBackend.service.ProjetoService;
import com.Trainee.ConectaTraineeBackend.model.Projeto;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    private static final Logger logger = LoggerFactory.getLogger(ProjetoController.class);

    @Autowired
    private ProjetoService projetoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Projeto>> listarTodos() {
        logger.info("Listando todos os projetos.");
        List<Projeto> projetos = projetoService.listarTodos();
        return ResponseEntity.ok(projetos);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Projeto> criarProjeto(@RequestBody ProjetoRequest request) {
        logger.info("🟢 Criando novo projeto: {}", request.getProjeto().getNome());

        Projeto novoProjeto = projetoService.salvarProjeto(request.getProjeto(), request.getUsuariosIds());

        return ResponseEntity.ok(novoProjeto);
    }


    @Autowired
    private ProjetoUsuarioRepository projetoUsuarioRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        Optional<Projeto> projeto = projetoService.buscarProjetoComUsuarios(id);
        return projeto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetoService.deletarProjeto(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizarProjeto(
            @PathVariable Long id,
            @RequestBody(required = false) ProjetoRequest request) {
        if (request == null || request.getProjeto() == null) {
            logger.error("❌ ERRO: O objeto 'ProjetoRequest' está NULL!");
            return ResponseEntity.badRequest().build();
        }

        logger.info("🔄 Recebida requisição para atualizar projeto: {}", request.getProjeto().getNome());

        Projeto projetoAtualizado = projetoService.atualizarProjeto(id, request.getProjeto(), request.getUsuariosIds());
        return ResponseEntity.ok(projetoAtualizado);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuariosDoProjeto(@PathVariable Long id) {
        logger.info("🔍 Buscando usuários vinculados ao projeto ID: {}", id);

        Optional<Projeto> projetoOpt = projetoService.buscarPorId(id);

        if (projetoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Usuario> usuarios = projetoOpt.get().getUsuarios();
        return ResponseEntity.ok(usuarios);
    }




}















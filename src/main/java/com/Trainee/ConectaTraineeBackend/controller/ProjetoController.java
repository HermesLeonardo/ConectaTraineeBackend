package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.DTO.ProjetoRequest;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
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

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando projeto com ID: {}", id);
        Optional<Projeto> projeto = projetoService.buscarPorId(id);

        if (projeto.isPresent()) {
            Projeto projetoEncontrado = projeto.get();

            // 🔥 Buscando corretamente os IDs dos usuários responsáveis
            List<Long> usuariosIds = projetoUsuarioRepository.findByProjetoId(id)
                    .stream()
                    .map(projetoUsuario -> projetoUsuario.getUsuario().getId())
                    .collect(Collectors.toList());

            projetoEncontrado.setIdUsuarioResponsavel(usuariosIds);

            logger.info("👥 IDs dos responsáveis carregados: {}", usuariosIds);
            return ResponseEntity.ok(projetoEncontrado);
        } else {
            logger.warn("Projeto com ID {} não encontrado.", id);
            return ResponseEntity.notFound().build();
        }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetoService.deletarProjeto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizarProjeto(@PathVariable Long id, @RequestBody Projeto projeto) {
        logger.info("Atualizando projeto com ID: {}", id);
        Optional<Projeto> projetoExistente = projetoService.buscarPorId(id);

        if (projetoExistente.isPresent()) {
            Projeto projetoAtualizado = projetoService.atualizarProjeto(id, projeto);
            logger.info("Projeto atualizado com sucesso: ID {}", id);
            return ResponseEntity.ok(projetoAtualizado);
        } else {
            logger.warn("Projeto com ID {} não encontrado.");
            return ResponseEntity.notFound().build();
        }
    }
}

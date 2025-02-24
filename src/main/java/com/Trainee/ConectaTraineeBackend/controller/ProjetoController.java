package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Projeto> criarProjeto(@RequestBody Projeto projeto) {
        logger.info("Criando novo projeto: {}", projeto.getNome());

        if (projeto.getUsuariosResponsaveis() == null || projeto.getUsuariosResponsaveis().isEmpty()) {
            logger.warn("Nenhum usuário responsável foi enviado.");
        } else {
            // 🔹 Convertendo lista de IDs para objetos Usuario
            List<Long> idsUsuarios = projeto.getUsuariosResponsaveis()
                    .stream()
                    .map(Usuario::getId)  // 🔴 ERRO: `projeto.getUsuariosResponsaveis()` pode estar vindo apenas com IDs, não objetos
                    .collect(Collectors.toList());

            List<Usuario> usuarios = usuarioRepository.findAllById(idsUsuarios);

            logger.info("Usuários encontrados no banco: {}",
                    usuarios.stream().map(Usuario::getNome).collect(Collectors.toList()));

            projeto.setUsuariosResponsaveis(usuarios); // 🔧 Vinculando usuários encontrados ao projeto
        }

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

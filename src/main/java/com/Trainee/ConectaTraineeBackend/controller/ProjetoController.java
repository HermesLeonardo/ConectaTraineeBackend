package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.DTO.ProjetoRequest;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoUsuarioRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.LancamentoHorasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
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
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Projeto>> listarTodos() {
        logger.info("Listando todos os projetos.");
        List<Projeto> projetos = projetoService.listarTodos();
        return ResponseEntity.ok(projetos);
    }

    @PostMapping
    public ResponseEntity<Projeto> criarProjeto(@RequestBody ProjetoRequest request) {
        logger.info("🟢 Criando novo projeto: {}", request.getProjeto().getNome());

        Projeto novoProjeto = projetoService.salvarProjeto(
                request.getProjeto(),
                request.getUsuariosIds(),
                request.getIdUsuarioResponsavel() // 🔹 Passamos o ID do ADMIN
        );

        return ResponseEntity.ok(novoProjeto);

    }



    @Autowired
    private ProjetoUsuarioRepository projetoUsuarioRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(@PathVariable Long id) {
        Optional<Projeto> projeto = projetoService.buscarProjetoComUsuarios(id);
        return projeto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProjeto(@PathVariable Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetoService.deletarProjeto(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizarProjeto(
            @PathVariable Long id,
            @RequestBody(required = false) ProjetoRequest request) {

        if (request == null || request.getProjeto() == null) {
            logger.error("❌ ERRO: O objeto 'ProjetoRequest' está NULL!");
            return ResponseEntity.badRequest().build();
        }
        logger.info("🔄 Recebida requisição para atualizar projeto ID {} pelo usuário {}", id, SecurityContextHolder.getContext().getAuthentication().getName());
        logger.info("🔄 Recebida requisição para atualizar projeto: {}", request.getProjeto().getNome());
        logger.info("📢 JSON recebido na atualização: {}", request);

        Projeto projetoAtualizado = projetoService.atualizarProjeto(
                id,
                request.getProjeto(),
                request.getUsuariosIds(),
                request.getIdUsuarioResponsavel()
        );

        return ResponseEntity.ok(projetoAtualizado);
    }


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

    @PutMapping("/{id}/responsavel")
    public ResponseEntity<Projeto> atualizarResponsavel(@PathVariable Long id, @RequestBody Long idNovoResponsavel) {
        logger.info("🔄 Atualizando responsável pelo projeto ID {}", id);

        Projeto projeto = projetoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        Usuario novoResponsavel = usuarioRepository.findById(idNovoResponsavel)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!"ADMIN".equals(novoResponsavel.getPerfil())) {
            return ResponseEntity.badRequest().body(projeto);
        }

        projeto.setUsuarioResponsavel(novoResponsavel);
        projetoService.salvarProjeto(projeto, null, idNovoResponsavel);

        return ResponseEntity.ok(projeto);
    }



    @GetMapping("/usuario-logado")
    public ResponseEntity<List<Projeto>> listarProjetosUsuarioLogado() {
        logger.info("➡️ Requisição recebida para listar projetos do usuário logado.");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("🔑 Extraindo email do usuário autenticado: {}", email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            logger.warn("⚠️ Nenhum usuário encontrado com o email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();
        logger.info("✅ Usuário autenticado: ID={}, Email={}, Perfil={}", usuario.getId(), usuario.getEmail(), usuario.getPerfil());

        List<Projeto> projetos;
        if (usuario.getPerfil().equals("ADMIN")) {
            projetos = projetoService.listarTodos(); // 🔹 Admin pode ver todos os projetos
        } else {
            projetos = projetoService.listarProjetosDoUsuario(usuario.getId()); // 🔹 Usuário comum só vê seus projetos
        }

        if (projetos.isEmpty()) {
            logger.warn("⚠ Nenhum projeto encontrado para o usuário {}.", usuario.getEmail());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }

        logger.info("📌 {} projetos encontrados para o usuário {}", projetos.size(), usuario.getEmail());
        return ResponseEntity.ok(projetos);
    }


    @Autowired
    private ProjetoService projetoService;

    @GetMapping("/total-horas-lancadas")
    public ResponseEntity<Double> obterTotalHorasLancadas() {
        double totalHoras = projetoService.calcularTotalHorasLancadas();
        return ResponseEntity.ok(totalHoras);
    }

    @GetMapping("/detalhes")
    public ResponseEntity<List<Projeto>> listarProjetosComDetalhes() {
        return ResponseEntity.ok(projetoService.listarProjetosComDetalhes());
    }

}















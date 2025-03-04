package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.DTO.AtividadeRequest;
import com.Trainee.ConectaTraineeBackend.enums.StatusAtividade;
import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.AtividadeRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/atividades")
@CrossOrigin(origins = "*")
public class AtividadeController {

    private static final Logger logger = LoggerFactory.getLogger(AtividadeController.class);

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AtividadeRepository atividadeRepository;


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Atividade>> listarTodos() {
        List<Atividade> atividades = atividadeService.listarTodos();

        // 🚀 Log para depuração das datas
        atividades.forEach(a -> logger.info("Atividade ID: {}, Data Início: {}, Data Fim: {}",
                a.getId(), a.getDataInicio(), a.getDataFim()));

        return ResponseEntity.ok(atividades);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando atividade com ID: {}", id);
        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(id);

        if (atividadeOpt.isEmpty()) {
            logger.warn("Atividade com ID {} não encontrada.", id);
            return ResponseEntity.notFound().build();
        }

        Atividade atividade = atividadeOpt.get();
        Set<Usuario> usuarios = atividadeService.listarUsuarios(id);
        atividade.setUsuariosResponsaveis(usuarios);

        return ResponseEntity.ok(atividade);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Atividade> criarAtividade(@RequestBody AtividadeRequest atividadeRequest) {
        logger.info("📥 Recebendo requisição para criar atividade.");
        logger.info("📥 IDs dos usuários recebidos no backend: {}", atividadeRequest.getUsuariosIds());

        Projeto projeto = projetoRepository.findById(atividadeRequest.getId_projeto())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        Atividade atividade = new Atividade();
        atividade.setProjeto(projeto);
        atividade.setNome(atividadeRequest.getNome());
        atividade.setDescricao(atividadeRequest.getDescricao());
        atividade.setStatus(StatusAtividade.valueOf(atividadeRequest.getStatus()));
        atividade.setDataInicio(LocalDate.parse(atividadeRequest.getData_inicio()));
        atividade.setDataFim(atividadeRequest.getData_fim() != null ? LocalDate.parse(atividadeRequest.getData_fim()) : null);

        // 🛑 Adicionando logs antes de buscar usuários
        Set<Long> usuariosIds = Optional.ofNullable(atividadeRequest.getUsuariosIds())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toSet());

        logger.info("🔍 IDs de usuários recebidos: {}", usuariosIds);

        // 💾 SALVANDO ATIVIDADE COM USUÁRIOS
        atividade = atividadeService.salvarAtividade(atividade, usuariosIds);

        // ✅ Logs para ver os usuários vinculados corretamente
        if (atividade.getUsuariosResponsaveis() != null && !atividade.getUsuariosResponsaveis().isEmpty()) {
            Atividade finalAtividade = atividade;
            atividade.getUsuariosResponsaveis().forEach(usuario ->
                    logger.info("✅ Usuário {} vinculado à atividade {}", usuario.getId(), finalAtividade.getNome()));
        } else {
            logger.warn("⚠ Nenhum usuário foi vinculado à atividade {}", atividade.getNome());
        }

        return ResponseEntity.ok(atividade);
    }



    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/usuarios")
    public ResponseEntity<Atividade> adicionarUsuarios(@PathVariable Long id, @RequestBody Set<Usuario> usuarios) {
        logger.info("Adicionando usuários à atividade {}", id);
        return ResponseEntity.ok(atividadeService.adicionarUsuarios(id, usuarios));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<Set<Usuario>> listarUsuarios(@PathVariable Long id) {
        logger.info("Listando usuários da atividade {}", id);
        return ResponseEntity.ok(atividadeService.listarUsuarios(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtividade(@PathVariable Long id) {
        logger.info("Deletando atividade com ID: {}", id);
        atividadeService.deletarAtividade(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Atividade> atualizarAtividade(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        logger.info("Atualizando atividade com ID: {}", id);

        Optional<Atividade> atividadeOptional = atividadeService.buscarPorId(id);
        if (atividadeOptional.isEmpty()) {
            logger.warn("Atividade com ID {} não encontrada.", id);
            return ResponseEntity.notFound().build();
        }

        Atividade atividade = atividadeOptional.get();

        // 🚀 **Correção: Verifica se os valores não são nulos antes de acessar**
        if (payload.containsKey("nome") && payload.get("nome") != null) {
            atividade.setNome(payload.get("nome").toString());
        }
        if (payload.containsKey("descricao") && payload.get("descricao") != null) {
            atividade.setDescricao(payload.get("descricao").toString());
        }
        if (payload.containsKey("status") && payload.get("status") != null) {
            try {
                atividade.setStatus(StatusAtividade.valueOf(payload.get("status").toString()));
            } catch (IllegalArgumentException e) {
                logger.error("Status inválido: {}", payload.get("status"));
                return ResponseEntity.badRequest().body(null);
            }
        }
        if (payload.containsKey("data_inicio") && payload.get("data_inicio") != null) {
            atividade.setDataInicio(LocalDate.parse(payload.get("data_inicio").toString()));
        }
        if (payload.containsKey("data_fim") && payload.get("data_fim") != null) {
            atividade.setDataFim(LocalDate.parse(payload.get("data_fim").toString()));
        }
        if (payload.containsKey("id_projeto") && payload.get("id_projeto") != null) {
            Long idProjeto = ((Number) payload.get("id_projeto")).longValue();
            atividade.setProjeto(projetoRepository.findById(idProjeto)
                    .orElseThrow(() -> new RuntimeException("Projeto não encontrado")));
        }

        if (payload.containsKey("usuariosIds") && payload.get("usuariosIds") != null) {
            Set<Long> usuariosIds = new HashSet<>();
            usuariosIds = ((List<Integer>) payload.get("usuariosIds")).stream().map(Long::valueOf).collect(Collectors.toSet());

            Set<Usuario> usuarios = new HashSet<>(usuarioRepository.findAllById(usuariosIds));
            atividade.setUsuariosResponsaveis(usuarios);
        }

        // 🚀 **Salva a atividade corrigida**
        atividade = atividadeService.salvarAtividade(atividade, atividade.getUsuariosResponsaveisIds());

        return ResponseEntity.ok(atividade);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/usuario-logado")
    public ResponseEntity<List<Atividade>> listarAtividadesUsuarioLogado() {
        logger.info("➡️ Requisição recebida para listar atividades do usuário logado.");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("🔑 Extraindo email do usuário autenticado: {}", email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.warn("⚠️ Nenhum usuário encontrado com o email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();
        logger.info("✅ Usuário autenticado: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        List<Atividade> atividades = atividadeRepository.buscarAtividadesDoUsuario(usuario.getId());

        logger.info("📌 {} atividades encontradas para o usuário {}", atividades.size(), usuario.getEmail());

        return ResponseEntity.ok(atividades);
    }







}

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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
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


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<Atividade>> listarTodos() {
        List<Atividade> atividades = atividadeService.listarTodos();

        // Garante que os usuários são carregados antes de retornar
        atividades.forEach(a -> a.getUsuariosResponsaveis().size());

        return ResponseEntity.ok(atividades);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Atividade> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando atividade com ID: {}", id);
        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(id);

        if (atividadeOpt.isEmpty()) {
            logger.warn("Atividade com ID {} não encontrada.", id);
            return ResponseEntity.notFound().build();
        }

        Atividade atividade = atividadeOpt.get();

        // Garante que o projeto seja carregado corretamente
        if (atividade.getProjeto() != null) {
            atividade.setProjeto(projetoRepository.findById(atividade.getProjeto().getId()).orElse(null));
        }

        return ResponseEntity.ok(atividade);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Atividade> criarAtividade(@RequestBody AtividadeRequest atividadeRequest) {
        logger.info("📥 Recebendo requisição para criar atividade.");
        logger.info("📥 JSON recebido no backend: {}", atividadeRequest);
        logger.info("🔑 Token recebido: {}", SecurityContextHolder.getContext().getAuthentication());

        // 🚀 Logs para depuração das datas antes da conversão
        logger.info("🔍 Data de Início recebida: {}", atividadeRequest.getData_inicio());
        logger.info("🔍 Data de Fim recebida: {}", atividadeRequest.getData_fim());

        Projeto projeto = projetoRepository.findById(atividadeRequest.getId_projeto())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        Atividade atividade = new Atividade();
        atividade.setProjeto(projeto);
        atividade.setNome(atividadeRequest.getNome());
        atividade.setDescricao(atividadeRequest.getDescricao());
        atividade.setStatus(StatusAtividade.valueOf(atividadeRequest.getStatus()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        atividade.setDataInicio(
                (atividadeRequest.getData_inicio() != null && !atividadeRequest.getData_inicio().isEmpty()) ?
                        LocalDate.parse(atividadeRequest.getData_inicio()) : null
        );

        atividade.setDataFim(
                (atividadeRequest.getData_fim() != null && !atividadeRequest.getData_fim().isEmpty()) ?
                        LocalDate.parse(atividadeRequest.getData_fim()) : null
        );


        // 🚀 Logs para confirmar valores após a conversão
        logger.info("✅ Data de Início após conversão: {}", atividade.getDataInicio());
        logger.info("✅ Data de Fim após conversão: {}", atividade.getDataFim());

        // 🛑 Adicionando logs antes de buscar usuários
        Set<Long> usuariosIds = Optional.ofNullable(atividadeRequest.getUsuariosIds())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toSet());

        logger.info("🔍 IDs de usuários recebidos: {}", usuariosIds);

        atividade = atividadeService.salvarAtividade(atividade, usuariosIds);

        if (atividade.getUsuariosResponsaveis() != null && !atividade.getUsuariosResponsaveis().isEmpty()) {
            atividade.getUsuariosResponsaveis().forEach(usuario ->
                    logger.info("✅ Usuário {} vinculado à atividade {}", usuario.getId()));
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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}/usuarios")
    public ResponseEntity<Set<Usuario>> listarUsuarios(@PathVariable Long id) {
        logger.info("Listando usuários da atividade {}", id);
        return ResponseEntity.ok(atividadeService.listarUsuarios(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    @CacheEvict(value = "atividades", allEntries = true)
    public ResponseEntity<?> deletarAtividade(@PathVariable Long id) {
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

        if (atividades.isEmpty()) {
            logger.warn("⚠ Nenhuma atividade encontrada para o usuário {}.", usuario.getEmail());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
        }

        logger.info("📌 {} atividades encontradas para o usuário {}", atividades.size(), usuario.getEmail());
        return ResponseEntity.ok(atividades);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}/usuarios-responsaveis")
    public ResponseEntity<Set<Usuario>> listarUsuariosDaAtividade(@PathVariable Long id) {
        logger.info("🔍 Buscando usuários vinculados à atividade ID: {}", id);

        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(id);

        if (atividadeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Set<Usuario> usuarios = atividadeOpt.get().getUsuariosResponsaveis();
        return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/desativar")
    public ResponseEntity<Map<String, String>> desativarAtividade(@PathVariable Long id) {
        Optional<Atividade> atividadeOpt = atividadeRepository.findById(id);
        if (atividadeOpt.isPresent()) {
            Atividade atividade = atividadeOpt.get();
            atividade.setAtivo(false);
            atividadeRepository.save(atividade);

            logger.info("✅ Atividade {} desativada com sucesso!", id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Atividade desativada com sucesso!");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Atividade não encontrada"));
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/reativar")
    public ResponseEntity<Map<String, String>> reativarAtividade(@PathVariable Long id) {
        Optional<Atividade> atividadeOpt = atividadeRepository.findById(id);

        if (atividadeOpt.isPresent()) {
            Atividade atividade = atividadeOpt.get();

            if (atividade.isAtivo()) {
                return ResponseEntity.badRequest().body(Map.of("message", "A atividade já está ativa!"));
            }

            atividade.setAtivo(true);
            atividadeRepository.save(atividade);

            logger.info("✅ Atividade {} reativada com sucesso!", id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Atividade reativada com sucesso!");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Atividade não encontrada"));
    }




}

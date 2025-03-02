package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.enums.StatusAtividade;
import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Atividade> criarAtividade(@RequestBody Map<String, Object> payload) {
        logger.info("Criando nova atividade.");

        Long idProjeto = ((Number) payload.get("id_projeto")).longValue();
        String nome = payload.get("nome").toString();
        String descricao = payload.get("descricao").toString();
        LocalDateTime dataInicio = LocalDateTime.parse(payload.get("data_inicio").toString());
        LocalDateTime dataFim = payload.get("data_fim") != null ? LocalDateTime.parse(payload.get("data_fim").toString()) : dataInicio.plusHours(1);
        StatusAtividade status = StatusAtividade.valueOf(payload.get("status").toString());

        // Convertendo usuariosIds para Set<Long>
        Set<Long> usuariosIds = new HashSet<>();
        if (payload.get("usuariosIds") != null) {
            usuariosIds = ((List<Integer>) payload.get("usuariosIds")).stream().map(Long::valueOf).collect(Collectors.toSet());
        }

        Atividade atividade = new Atividade();
        atividade.setProjeto(projetoRepository.findById(idProjeto).orElseThrow(() -> new RuntimeException("Projeto não encontrado")));
        atividade.setNome(nome);
        atividade.setDescricao(descricao);
        atividade.setDataInicio(LocalDate.from(dataInicio));
        atividade.setDataFim(LocalDate.from(dataFim));
        atividade.setStatus(status);

        // Chama o service passando os usuários
        atividade = atividadeService.salvarAtividade(atividade, usuariosIds);

        return ResponseEntity.ok(atividade);
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

    @PutMapping("/{id}")
    public ResponseEntity<Atividade> atualizarAtividade(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        logger.info("Atualizando atividade com ID: {}", id);

        Optional<Atividade> atividadeOptional = atividadeService.buscarPorId(id);
        if (atividadeOptional.isEmpty()) {
            logger.warn("Atividade com ID {} não encontrada.", id);
            return ResponseEntity.notFound().build();
        }

        Atividade atividade = atividadeOptional.get();

        if (payload.containsKey("nome")) {
            atividade.setNome(payload.get("nome").toString());
        }
        if (payload.containsKey("descricao")) {
            atividade.setDescricao(payload.get("descricao").toString());
        }
        if (payload.containsKey("status")) {
            atividade.setStatus(StatusAtividade.valueOf(payload.get("status").toString()));
        }
        if (payload.containsKey("data_inicio")) {
            atividade.setDataInicio(LocalDate.parse(payload.get("data_inicio").toString())); // 🔹 Correção
        }
        if (payload.containsKey("data_fim")) {
            atividade.setDataFim(LocalDate.parse(payload.get("data_fim").toString())); // 🔹 Correção
        }
        if (payload.containsKey("id_projeto")) {
            Long idProjeto = ((Number) payload.get("id_projeto")).longValue();
            atividade.setProjeto(projetoRepository.findById(idProjeto).orElseThrow(() -> new RuntimeException("Projeto não encontrado")));
        }

        if (payload.containsKey("usuariosIds")) {
            Set<Long> usuariosIds = new HashSet<>();
            if (payload.get("usuariosIds") != null) {
                usuariosIds = ((List<Integer>) payload.get("usuariosIds")).stream().map(Long::valueOf).collect(Collectors.toSet());
            }

            // 🔹 Buscar os usuários para garantir que são válidos antes de salvar
            Set<Usuario> usuarios = new HashSet<>(usuarioRepository.findAllById(usuariosIds));

            if (!usuarios.isEmpty()) {
                atividade.setUsuariosResponsaveis(usuarios);
            } else {
                logger.warn("Nenhum usuário encontrado para os IDs: {}", usuariosIds);
                atividade.setUsuariosResponsaveis(new HashSet<>());
            }
        }


        return ResponseEntity.ok(atividade);
    }




}

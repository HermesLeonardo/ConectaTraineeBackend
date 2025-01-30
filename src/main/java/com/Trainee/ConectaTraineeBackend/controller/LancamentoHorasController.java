package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;
import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.service.LancamentoHorasService;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import com.Trainee.ConectaTraineeBackend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;  
import java.util.HashMap;

@RestController
@RequestMapping("/api/lancamentos-horas")
public class LancamentoHorasController {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoHorasController.class);

    @Autowired
    private LancamentoHorasService lancamentoHorasService;

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<LancamentoHoras> registrarLancamento(@RequestBody Map<String, String> request) {
        logger.info("Registrando novo lançamento de horas.");

        Long idAtividade = Long.parseLong(request.get("idAtividade"));
        Long idUsuario = Long.parseLong(request.get("idUsuario"));
        String descricao = request.get("descricao");
        LocalDateTime dataInicio = LocalDateTime.parse(request.get("dataInicio"));
        LocalDateTime dataFim = LocalDateTime.parse(request.get("dataFim"));

        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(idAtividade);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(idUsuario);

        if (atividadeOpt.isEmpty() || usuarioOpt.isEmpty()) {
            logger.warn("Atividade ou usuário não encontrados.");
            return ResponseEntity.badRequest().build();
        }

        Atividade atividade = atividadeOpt.get();
        Usuario usuario = usuarioOpt.get();

        LancamentoHoras novoLancamento = new LancamentoHoras(atividade, usuario, descricao, dataInicio, dataFim);

        return ResponseEntity.ok(lancamentoHorasService.salvarLancamento(novoLancamento));
    }


    @GetMapping("/{id}")
    public ResponseEntity<LancamentoHoras> buscarPorId(@PathVariable Long id) {
        logger.info("Buscando lançamento de horas com ID: {}", id);
        Optional<LancamentoHoras> lancamento = lancamentoHorasService.buscarPorId(id);
        return lancamento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLancamento(@PathVariable Long id) {
        logger.info("Deletando lançamento de horas com ID: {}", id);
        lancamentoHorasService.deletarLancamento(id);
        return ResponseEntity.noContent().build();
    }
}

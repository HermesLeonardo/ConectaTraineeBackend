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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // Captura apenas o dia/mês e adiciona o ano atual
        String[] dataParts = request.get("dataInicio").split("/");
        int dia = Integer.parseInt(dataParts[0]);
        int mes = Integer.parseInt(dataParts[1]);
        int ano = LocalDateTime.now().getYear();

        LocalDateTime dataInicio = LocalDateTime.of(ano, mes, dia, Integer.parseInt(request.get("horaInicio").split(":")[0]), 0);
        LocalDateTime dataFim = LocalDateTime.of(ano, mes, dia, Integer.parseInt(request.get("horaFim").split(":")[0]), 0);

        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(idAtividade);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(idUsuario);

        if (atividadeOpt.isEmpty() || usuarioOpt.isEmpty()) {
            logger.warn("Atividade ou usuário não encontrados.");
            return ResponseEntity.badRequest().build();
        }

        LancamentoHoras novoLancamento = new LancamentoHoras(atividadeOpt.get(), usuarioOpt.get(), descricao, dataInicio, dataFim);

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


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<LancamentoHoras>> listarLancamentos(@RequestParam(required = false) Long usuarioId) {
        logger.info("📄 Buscando lançamentos de horas");

        List<LancamentoHoras> lancamentos;

        if (usuarioId != null) {
            logger.info("🔍 Listando lançamentos do usuário ID: {}", usuarioId);
            lancamentos = lancamentoHorasService.buscarLancamentosPorUsuario(usuarioId);
        } else {
            logger.info("🔍 Listando TODOS os lançamentos (Apenas ADMIN)");
            lancamentos = lancamentoHorasService.listarTodos();
        }

        return ResponseEntity.ok(lancamentos);
    }



    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarLancamento(@PathVariable Long id) {
        logger.info("🛑 Cancelando lançamento de horas com ID: {}", id);

        Optional<LancamentoHoras> lancamentoOpt = lancamentoHorasService.buscarPorId(id);
        if (lancamentoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LancamentoHoras lancamento = lancamentoOpt.get();
        lancamento.setCancelado(true); // 🔹 Definir como cancelado
        lancamentoHorasService.salvarLancamento(lancamento); // 🔹 Atualizar no banco

        return ResponseEntity.ok().build();
    }






}

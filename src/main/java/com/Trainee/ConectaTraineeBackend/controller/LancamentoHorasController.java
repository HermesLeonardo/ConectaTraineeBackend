package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.DTO.LancamentoHorasRequest;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos-horas")
@CrossOrigin(origins = "*")
public class LancamentoHorasController {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoHorasController.class);

    private final AtividadeService atividadeService;
    private final UsuarioService usuarioService;

    public LancamentoHorasController(LancamentoHorasService lancamentoHorasService,
                                     AtividadeService atividadeService,
                                     UsuarioService usuarioService) {
        this.lancamentoHorasService = lancamentoHorasService;
        this.atividadeService = atividadeService;
        this.usuarioService = usuarioService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<LancamentoHoras> registrarLancamento(@RequestBody LancamentoHorasRequest request) {
        logger.info("📌 Registrando novo lançamento de horas.");

        if (request.getIdAtividade() == null) {
            logger.warn("⚠ ID da Atividade não informado.");
            return ResponseEntity.badRequest().build();
        }

        // 🔍 Obter o email do usuário autenticado
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
            logger.warn("⚠ Usuário autenticado não encontrado.");
            return ResponseEntity.status(401).build();
        }

        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(request.getIdAtividade());

        if (atividadeOpt.isEmpty()) {
            logger.warn("⚠ Atividade não encontrada.");
            return ResponseEntity.badRequest().build();
        }

        try {
            // ✅ Converte a data e hora separadamente
            String[] dataParts = request.getDataInicio().split("/");
            int dia = Integer.parseInt(dataParts[0]);
            int mes = Integer.parseInt(dataParts[1]);
            int ano = LocalDateTime.now().getYear(); // Assumimos o ano atual

            String[] horaInicioParts = request.getHoraInicio().split(":");
            int horaInicio = Integer.parseInt(horaInicioParts[0]);
            int minutoInicio = Integer.parseInt(horaInicioParts[1]);

            String[] horaFimParts = request.getHoraFim().split(":");
            int horaFim = Integer.parseInt(horaFimParts[0]);
            int minutoFim = Integer.parseInt(horaFimParts[1]);

            LocalDateTime dataInicio = LocalDateTime.of(ano, mes, dia, horaInicio, minutoInicio);
            LocalDateTime dataFim = LocalDateTime.of(ano, mes, dia, horaFim, minutoFim);

            // Criar novo lançamento de horas
            LancamentoHoras novoLancamento = new LancamentoHoras(
                    atividadeOpt.get(),
                    usuarioOpt.get(),
                    request.getDescricao(),
                    dataInicio,
                    dataFim
            );

            LancamentoHoras salvo = lancamentoHorasService.salvarLancamento(novoLancamento);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            logger.error("❌ Erro ao processar datas: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

    }

    @Autowired
    private LancamentoHorasService lancamentoHorasService;


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/total-horas-lancadas")
    public ResponseEntity<Double> obterTotalHorasLancadas() {
        double totalHoras = lancamentoHorasService.calcularTotalHorasLancadas();
        return ResponseEntity.ok(totalHoras);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<LancamentoHoras> buscarPorId(@PathVariable Long id) {
        logger.info("📄 Buscando lançamento de horas com ID: {}", id);
        Optional<LancamentoHoras> lancamento = lancamentoHorasService.buscarPorId(id);
        return lancamento.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<LancamentoHoras>> listarLancamentos(@RequestParam(required = false) Long usuarioId) {
        logger.info("📄 Buscando lançamentos de horas");

        List<LancamentoHoras> lancamentos = (usuarioId != null)
                ? lancamentoHorasService.buscarLancamentosPorUsuario(usuarioId)
                : lancamentoHorasService.listarTodos();

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
        lancamento.setCancelado(true);
        lancamentoHorasService.atualizarLancamento(lancamento);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarLancamento(@PathVariable Long id) {
        logger.info("🗑 Deletando lançamento de horas com ID: {}", id);
        lancamentoHorasService.deletarLancamento(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/atividades")
    public ResponseEntity<?> listarAtividadesDoUsuarioLogado() {
        logger.info("➡️ Requisição recebida para listar atividades do usuário logado.");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("🔑 Extraindo email do usuário autenticado: {}", email);
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (usuarioOpt.isEmpty()) {
            logger.warn("⚠️ Nenhum usuário encontrado com o email: {}", email);
            return ResponseEntity.status(401).body("Usuário não encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        logger.info("✅ Usuário autenticado: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        List<Atividade> atividades = atividadeService.buscarAtividadesPorUsuario(usuario.getId());

        if (atividades.isEmpty()) {
            logger.warn("⚠ Nenhuma atividade encontrada para o usuário {}.", usuario.getEmail());
            return ResponseEntity.status(204).body("Nenhuma atividade encontrada.");
        }

        logger.info("📌 {} atividades encontradas para o usuário {}", atividades.size(), usuario.getEmail());
        return ResponseEntity.ok(atividades);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/ultimos-lancamentos")
    public ResponseEntity<List<LancamentoHoras>> obterUltimosLancamentos(
            @RequestParam(value = "limite", defaultValue = "5") int limite) {

        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        Usuario usuario = usuarioOpt.get();
        List<LancamentoHoras> ultimosLancamentos;

        if ("ADMIN".equals(usuario.getPerfil())) {
            ultimosLancamentos = lancamentoHorasService.buscarUltimosLancamentos(limite);
        } else {
            ultimosLancamentos = lancamentoHorasService.buscarUltimosLancamentosPorUsuario(usuario.getId(), limite);
        }

        return ResponseEntity.ok(ultimosLancamentos);
    }

    @GetMapping("/usuario-logado")
    public ResponseEntity<List<LancamentoHoras>> listarLancamentosUsuarioLogado() {
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(emailUsuario);

        if (usuarioOpt.isEmpty()) {
            logger.warn("⚠️ Nenhum usuário encontrado com o email: {}", emailUsuario);
            return ResponseEntity.status(401).body(List.of());
        }

        Usuario usuario = usuarioOpt.get();
        logger.info("✅ Usuário autenticado: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        List<LancamentoHoras> lancamentos = lancamentoHorasService.buscarLancamentosPorUsuario(usuario.getId());

        if (lancamentos.isEmpty()) {
            logger.warn("⚠ Nenhum lançamento encontrado para o usuário {}.", usuario.getEmail());
            return ResponseEntity.status(204).body(List.of());
        }

        logger.info("📌 {} lançamentos encontrados para o usuário {}", lancamentos.size(), usuario.getEmail());
        return ResponseEntity.ok(lancamentos);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/cancelados")
    public ResponseEntity<List<LancamentoHoras>> listarLancamentosCancelados() {
        logger.info("📌 Buscando lançamentos de horas cancelados");

        List<LancamentoHoras> lancamentosCancelados = lancamentoHorasService.buscarLancamentosCancelados();

        if (lancamentosCancelados.isEmpty()) {
            logger.warn("⚠ Nenhum lançamento cancelado encontrado.");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(lancamentosCancelados);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/restaurar")
    public ResponseEntity<Void> restaurarLancamento(@PathVariable Long id) {
        logger.info("♻️ Restaurando lançamento de horas com ID: {}", id);

        Optional<LancamentoHoras> lancamentoOpt = lancamentoHorasService.buscarPorId(id);
        if (lancamentoOpt.isEmpty()) {
            logger.warn("⚠ Lançamento não encontrado.");
            return ResponseEntity.notFound().build();
        }

        LancamentoHoras lancamento = lancamentoOpt.get();
        lancamento.setCancelado(false);  // 🔹 Define como não cancelado
        lancamentoHorasService.atualizarLancamento(lancamento);

        return ResponseEntity.ok().build();
    }



}

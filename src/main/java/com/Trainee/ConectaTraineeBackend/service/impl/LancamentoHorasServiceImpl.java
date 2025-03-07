package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.DTO.LancamentoHorasRequest;
import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.LancamentoHorasRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import com.Trainee.ConectaTraineeBackend.service.LancamentoHorasService;
import com.Trainee.ConectaTraineeBackend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LancamentoHorasServiceImpl implements LancamentoHorasService {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoHorasServiceImpl.class);

    @Autowired
    private LancamentoHorasRepository lancamentoHorasRepository;

    @Autowired
    private AtividadeService atividadeService;

    @Autowired
    private UsuarioService usuarioService;



    @Override
    public LancamentoHoras salvarLancamento(LancamentoHorasRequest request) {
        logger.info("Registrando novo lançamento de horas.");

        // Buscar atividade e usuário pelo ID
        Optional<Atividade> atividadeOpt = atividadeService.buscarPorId(request.getIdAtividade());
        // 🔍 Obter o email do usuário autenticado
        String emailUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(emailUsuario);

        if (atividadeOpt.isEmpty() || usuarioOpt.isEmpty()) {
            logger.warn("Atividade ou usuário não encontrados.");
            throw new IllegalArgumentException("Atividade ou usuário não encontrados.");
        }

        try {
            // 🔹 Converter "dd/MM" para LocalDate
            String[] dataParts = request.getDataInicio().split("/");
            int dia = Integer.parseInt(dataParts[0]);
            int mes = Integer.parseInt(dataParts[1]);
            int ano = LocalDateTime.now().getYear(); // Assume o ano atual

            // 🔹 Converter horas para LocalTime
            String[] horaInicioParts = request.getHoraInicio().split(":");
            String[] horaFimParts = request.getHoraFim().split(":");

            int horaInicio = Integer.parseInt(horaInicioParts[0]);
            int minutoInicio = Integer.parseInt(horaInicioParts[1]);

            int horaFim = Integer.parseInt(horaFimParts[0]);
            int minutoFim = Integer.parseInt(horaFimParts[1]);

            // 🔹 Criar LocalDateTime combinando data + hora
            LocalDateTime dataInicio = LocalDateTime.of(ano, mes, dia, horaInicio, minutoInicio);
            LocalDateTime dataFim = LocalDateTime.of(ano, mes, dia, horaFim, minutoFim);

            // Criar e salvar novo lançamento de horas
            LancamentoHoras novoLancamento = new LancamentoHoras(
                    atividadeOpt.get(),
                    usuarioOpt.get(),
                    request.getDescricao(),
                    dataInicio,
                    dataFim
            );

            logger.info("✅ Lançamento criado com sucesso para atividade ID: {} e usuário ID: {}",
                    request.getIdAtividade(), usuarioOpt.get().getId());

            return lancamentoHorasRepository.save(novoLancamento);

        } catch (Exception e) {
            logger.error("❌ Erro ao converter data/hora: {}", e.getMessage());
            throw new IllegalArgumentException("Erro ao processar datas e horários.");
        }
    }




    @Override
    public Optional<LancamentoHoras> buscarPorId(Long id) {
        logger.info("Buscando lançamento de horas com ID: {}", id);
        return lancamentoHorasRepository.findById(id);
    }

    @Override
    public List<LancamentoHoras> listarTodos() {
        logger.info("Listando todos os lançamentos de horas ATIVOS.");
        return lancamentoHorasRepository.findByCanceladoFalse();
    }

    @Override
    public void deletarLancamento(Long id) {
        logger.info("Deletando lançamento de horas com ID: {}", id);
        lancamentoHorasRepository.deleteById(id);
    }

    @Override
    public List<LancamentoHoras> buscarLancamentosPorUsuario(Long usuarioId) {
        logger.info("📄 Buscando lançamentos ativos do usuário ID: {}", usuarioId);
        return lancamentoHorasRepository.buscarLancamentosAtivosPorUsuario(usuarioId);
    }

    @Override
    public LancamentoHoras atualizarLancamento(LancamentoHoras lancamento) {
        logger.info("🔄 Atualizando lançamento de horas ID: {}", lancamento.getId());
        return lancamentoHorasRepository.save(lancamento);
    }

    @Override
    public LancamentoHoras salvarLancamento(LancamentoHoras lancamento) {
        logger.info("✅ Salvando lançamento de horas...");
        return lancamentoHorasRepository.save(lancamento);
    }


}

package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;
import com.Trainee.ConectaTraineeBackend.repository.LancamentoHorasRepository;
import com.Trainee.ConectaTraineeBackend.service.LancamentoHorasService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LancamentoHorasServiceImpl implements LancamentoHorasService {

    private static final Logger logger = LoggerFactory.getLogger(LancamentoHorasServiceImpl.class);

    @Autowired
    private LancamentoHorasRepository lancamentoHorasRepository;

    @Override
    public LancamentoHoras salvarLancamento(LancamentoHoras lancamento) {
        logger.info("Registrando novo lançamento de horas.");
    
        if (lancamento.getAtividade() == null || lancamento.getUsuario() == null) {
            throw new IllegalArgumentException("Atividade ou Usuário não podem ser nulos.");
        }
    
        logger.info("Lançamento vinculado à atividade ID: {}", lancamento.getAtividade().getId());
        logger.info("Usuário registrando horas: ID {}", lancamento.getUsuario().getId());
    
        return lancamentoHorasRepository.save(lancamento);
    }

    @Override
    public Optional<LancamentoHoras> buscarPorId(Long id) {
        logger.info("Buscando lançamento de horas com ID: {}", id);
        return lancamentoHorasRepository.findById(id);
    }

    @Override
    public List<LancamentoHoras> listarTodos() {
        logger.info("Listando todos os lançamentos de horas.");
        return lancamentoHorasRepository.findAll();
    }

    @Override
    public void deletarLancamento(Long id) {
        logger.info("Deletando lançamento de horas com ID: {}", id);
        lancamentoHorasRepository.deleteById(id);
    }
}

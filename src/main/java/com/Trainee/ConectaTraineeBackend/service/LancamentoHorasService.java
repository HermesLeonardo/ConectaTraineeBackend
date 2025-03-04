package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;

import java.util.List;
import java.util.Optional;

public interface LancamentoHorasService {
    LancamentoHoras salvarLancamento(LancamentoHoras lancamento);
    Optional<LancamentoHoras> buscarPorId(Long id);
    List<LancamentoHoras> listarTodos();
    void deletarLancamento(Long id);

    List<LancamentoHoras> buscarLancamentosPorUsuario(Long usuarioId);
}

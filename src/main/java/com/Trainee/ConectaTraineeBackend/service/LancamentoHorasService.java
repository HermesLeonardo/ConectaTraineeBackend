package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.DTO.LancamentoHorasRequest;
import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LancamentoHorasService {


    LancamentoHoras salvarLancamento(LancamentoHorasRequest request);
    Optional<LancamentoHoras> buscarPorId(Long id);
    List<LancamentoHoras> listarTodos();
    void deletarLancamento(Long id);
    List<LancamentoHoras> buscarLancamentosPorUsuario(Long usuarioId);
    LancamentoHoras atualizarLancamento(LancamentoHoras lancamento);
    LancamentoHoras salvarLancamento(LancamentoHoras lancamento);


    double calcularTotalHorasLancadas();

    List<LancamentoHoras> buscarUltimosLancamentos(int limite);

    @Query("SELECT l FROM LancamentoHoras l WHERE l.usuario.id = :usuarioId AND l.cancelado = false ORDER BY l.dataInicio DESC")
    List<LancamentoHoras> buscarUltimosLancamentosPorUsuario(Long usuarioId, int limite);

}

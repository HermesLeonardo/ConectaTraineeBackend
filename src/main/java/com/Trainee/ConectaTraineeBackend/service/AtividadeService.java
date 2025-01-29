package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import java.util.List;
import java.util.Optional;

public interface AtividadeService {
    Atividade salvarAtividade(Atividade atividade);
    Optional<Atividade> buscarPorId(Long id);
    List<Atividade> listarTodos();
    void deletarAtividade(Long id);
    List<Atividade> buscarPorProjeto(Long idProjeto);
}

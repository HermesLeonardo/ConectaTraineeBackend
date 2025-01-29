package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import java.util.List;
import java.util.Optional;

public interface ProjetoService {
    Projeto salvarProjeto(Projeto projeto);
    Optional<Projeto> buscarPorId(Long id);
    List<Projeto> listarTodos();
    void deletarProjeto(Long id);
}

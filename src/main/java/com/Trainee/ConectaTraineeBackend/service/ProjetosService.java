package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Projetos;


import java.util.List;
import java.util.Optional;

public interface ProjetosService {
    Projetos salvarProjeto(Projetos projetos);
    Optional<Projetos> buscarPorId(Long id);
    List<Projetos> listarTodos();
    void deletarProjeto(Long id);

}

package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AtividadeService {
    Atividade salvarAtividade(Atividade atividade, Set<Long> usuariosIds);
    Optional<Atividade> buscarPorId(Long id);
    List<Atividade> listarTodos();
    void deletarAtividade(Long id);
    List<Atividade> buscarPorProjeto(Long idProjeto);


    Atividade adicionarUsuarios(Long id, Set<Usuario> usuarios);

    Atividade removerUsuario(Long id, Long idUsuario);

    Set<Usuario> listarUsuarios(Long id);
}


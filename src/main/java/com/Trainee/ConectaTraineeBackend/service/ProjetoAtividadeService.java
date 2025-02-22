package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;

import java.util.Set;

public interface ProjetoAtividadeService {

    Atividade adicionarUsuarios(Long id, Set<Usuario> usuarios);

    Atividade removerUsuario(Long id, Long idUsuario);

    Set<Usuario> listarUsuarios(Long id);
}

package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import java.util.Set;

public interface AtividadeUsuarioService {


    Atividade adicionarUsuarios(Long idAtividade, Set<Usuario> usuarios);


    Atividade removerUsuario(Long idAtividade, Long idUsuario);


    Set<Usuario> listarUsuarios(Long idAtividade);
}

package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.model.Atividade;
import java.util.*;


import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario salvarUsuario(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
    void deletarUsuario(Long id);

    Set<Atividade> listarAtividades(Long id);

}

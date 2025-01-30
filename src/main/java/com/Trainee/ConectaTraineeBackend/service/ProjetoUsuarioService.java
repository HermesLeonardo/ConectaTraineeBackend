package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import java.util.List;

public interface ProjetoUsuarioService {
    ProjetoUsuario adicionarUsuarioAoProjeto(Long idProjeto, Long idUsuario);
    List<ProjetoUsuario> listarProjetosPorUsuario(Long idUsuario);
    List<ProjetoUsuario> listarUsuariosPorProjeto(Long idProjeto);
}

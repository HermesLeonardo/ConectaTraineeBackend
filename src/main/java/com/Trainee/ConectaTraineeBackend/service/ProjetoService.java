package com.Trainee.ConectaTraineeBackend.service;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import java.util.List;
import java.util.Optional;

public interface ProjetoService {

    Projeto salvarProjeto(Projeto projeto, List<Long> usuariosIds); // Método original
    Projeto salvarProjeto(Projeto projeto, List<Long> usuariosIds, Long idUsuarioResponsavel); // Suporte ao responsável

    Optional<Projeto> buscarPorId(Long id);
    List<Projeto> listarTodos();

    Projeto atualizarProjeto(Long id, Projeto projetoAtualizado, List<Long> usuariosIds, Long responsavelId); // ✅ Mantemos apenas esta versão

    void deletarProjeto(Long id);
    Optional<Projeto> buscarProjetoComUsuarios(Long id);
}

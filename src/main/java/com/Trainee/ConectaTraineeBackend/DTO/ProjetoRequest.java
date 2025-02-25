package com.Trainee.ConectaTraineeBackend.DTO;

import com.Trainee.ConectaTraineeBackend.model.Projeto;

import java.util.List;

public class ProjetoRequest {
    private Projeto projeto;
    private List<Long> usuariosIds;

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public List<Long> getUsuariosIds() {
        return usuariosIds;
    }

    public void setUsuariosIds(List<Long> usuariosIds) {
        this.usuariosIds = usuariosIds;
    }
}

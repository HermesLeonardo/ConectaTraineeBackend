package com.Trainee.ConectaTraineeBackend.DTO;

import com.Trainee.ConectaTraineeBackend.model.Projeto;

import java.util.List;

public class ProjetoRequest {
    private Projeto projeto;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;
    private String status;
    private String prioridade;
    private List<Long> usuariosResponsaveisIds;
    private Long responsavelId;



    public Long getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Long responsavelId) {
        this.responsavelId = responsavelId;
    }


    public List<Long> getUsuariosResponsaveisIds() {
        return usuariosResponsaveisIds;
    }

    public void setUsuariosResponsaveisIds(List<Long> usuariosResponsaveisIds) {
        this.usuariosResponsaveisIds = usuariosResponsaveisIds;
    }




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




    private Long idUsuarioResponsavel; // Novo campo para receber o ID do ADMIN responsável

    public Long getIdUsuarioResponsavel() {
        return idUsuarioResponsavel;
    }

    public void setIdUsuarioResponsavel(Long idUsuarioResponsavel) {
        this.idUsuarioResponsavel = idUsuarioResponsavel;
    }



}

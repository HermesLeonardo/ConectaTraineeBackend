package com.Trainee.ConectaTraineeBackend.DTO;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjetoRequest {
    private Projeto projeto;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;
    private String status;
    private String prioridade;
    private List<Long> usuariosResponsaveisIds;
    private List<Long> usuariosIds = new ArrayList<>();
    private Long idUsuarioResponsavel;

    public Long getIdUsuarioResponsavel() {
        return idUsuarioResponsavel;
    }

    @JsonSetter("idUsuarioResponsavel")
    public void setIdUsuarioResponsavel(Object idUsuarioResponsavel) {
        if (idUsuarioResponsavel instanceof Number) {
            this.idUsuarioResponsavel = ((Number) idUsuarioResponsavel).longValue();
        } else {
            this.idUsuarioResponsavel = null;
        }
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public List<Long> getUsuariosIds() {
        return usuariosIds;
    }

    @JsonSetter("usuariosIds")
    public void setUsuariosIds(Object usuariosIds) {
        if (usuariosIds instanceof List) {
            this.usuariosIds = ((List<?>) usuariosIds).stream()
                    .filter(Objects::nonNull)
                    .map(id -> id instanceof Number ? ((Number) id).longValue() : null)
                    .collect(Collectors.toList());
        } else if (usuariosIds instanceof Number) {
            this.usuariosIds = List.of(((Number) usuariosIds).longValue());
        } else {
            this.usuariosIds = new ArrayList<>();
        }
    }


    public List<Long> getUsuariosResponsaveisIds() {
        return usuariosResponsaveisIds;
    }

    public void setUsuariosResponsaveisIds(List<Long> usuariosResponsaveisIds) {
        this.usuariosResponsaveisIds = usuariosResponsaveisIds;
    }
}

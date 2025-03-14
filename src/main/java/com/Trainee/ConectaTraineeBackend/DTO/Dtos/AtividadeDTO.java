package com.Trainee.ConectaTraineeBackend.DTO.Dtos;

import com.Trainee.ConectaTraineeBackend.model.Atividade;

public class AtividadeDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String status;

    public AtividadeDTO(Atividade atividade) {
        this.id = atividade.getId();
        this.nome = atividade.getNome();
        this.descricao = atividade.getDescricao();
        this.status = atividade.getStatus().toString();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getStatus() {
        return status;
    }
}

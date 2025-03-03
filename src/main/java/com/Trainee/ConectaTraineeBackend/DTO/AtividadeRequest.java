package com.Trainee.ConectaTraineeBackend.DTO;

import java.util.ArrayList;
import java.util.List;

public class AtividadeRequest {
    private Long id_projeto;
    private String nome;
    private String descricao;
    private String data_inicio;
    private String data_fim;
    private String status;

    // 🚀 Corrigido: Agora a lista é inicializada para evitar NullPointerException
    private List<Long> usuariosIds = new ArrayList<>();

    public Long getId_projeto() {
        return id_projeto;
    }

    public void setId_projeto(Long id_projeto) {
        this.id_projeto = id_projeto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData_inicio() {
        return data_inicio;
    }

    public void setData_inicio(String data_inicio) {
        this.data_inicio = data_inicio;
    }

    public String getData_fim() {
        return data_fim;
    }

    public void setData_fim(String data_fim) {
        this.data_fim = data_fim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getUsuariosIds() {
        return usuariosIds;
    }

    public void setUsuariosIds(List<Long> usuariosIds) {
        this.usuariosIds = usuariosIds != null ? usuariosIds : new ArrayList<>();
    }
}

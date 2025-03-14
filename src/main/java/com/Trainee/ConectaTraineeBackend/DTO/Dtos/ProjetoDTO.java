package com.Trainee.ConectaTraineeBackend.DTO.Dtos;


import com.Trainee.ConectaTraineeBackend.model.Projeto;

import java.util.List;
import java.util.stream.Collectors;

public class ProjetoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private List<UsuarioDTO> usuarios;
    private List<AtividadeDTO> atividades;

    public ProjetoDTO(Projeto projeto) {
        this.id = projeto.getId();
        this.nome = projeto.getNome();
        this.descricao = projeto.getDescricao();
        this.usuarios = projeto.getUsuarios().stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
        this.atividades = projeto.getAtividades().stream()
                .map(AtividadeDTO::new)
                .collect(Collectors.toList());
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

    public List<UsuarioDTO> getUsuarios() {
        return usuarios;
    }

    public List<AtividadeDTO> getAtividades() {
        return atividades;
    }
}

package com.Trainee.ConectaTraineeBackend.model;

import com.Trainee.ConectaTraineeBackend.enums.StatusAtividade;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "atividades")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    @JsonManagedReference
    @JsonIgnore  // 🔹 Evita recursão infinita
    private Projeto projeto;

    @NotNull(message = "Nome da atividade é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Setter
    @Column(nullable = false)
    private LocalDate dataInicio;

    @Setter
    @Column
    private LocalDate dataFim;

    @NotNull(message = "Status da atividade é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtividade status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "atividades_usuarios",
            joinColumns = @JoinColumn(name = "id_atividade"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private Set<Usuario> usuariosResponsaveis = new HashSet<>();


    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    private Boolean ativo = true;


    public Atividade() {}

    public Atividade(Projeto projeto, String nome, String descricao, StatusAtividade status, LocalDate dataInicio, LocalDate dataFim) {
        this.projeto = projeto;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.dataCriacao = LocalDateTime.now();
        this.dataInicio = dataInicio; // Corrigido
        this.dataFim = dataFim;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Projeto getProjeto() { return projeto; }
    public void setProjeto(Projeto projeto) { this.projeto = projeto; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // ✅ Corrigido para LocalDate
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }

    public StatusAtividade getStatus() { return status; }

    public void setStatus(StatusAtividade status) {
        this.status = status;

        if (status == StatusAtividade.CONCLUIDA && this.dataFim == null) {
            this.dataFim = LocalDate.now(); // 🔹 Só define se ainda não tiver um valor
        }
    }


    public Set<Usuario> getUsuariosResponsaveis() { return usuariosResponsaveis; }
    public void setUsuariosResponsaveis(Set<Usuario> usuariosResponsaveis) { this.usuariosResponsaveis = usuariosResponsaveis; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public Set<Long> getUsuariosResponsaveisIds() {
        return usuariosResponsaveis.stream().map(Usuario::getId).collect(Collectors.toSet());
    }
}

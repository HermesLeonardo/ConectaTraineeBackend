package com.Trainee.ConectaTraineeBackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.Trainee.ConectaTraineeBackend.enums.StatusProjeto;
import com.Trainee.ConectaTraineeBackend.enums.PrioridadeProjeto;

@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeProjeto prioridade;

    // Se quisermos manter um responsável principal para o projeto:
    @ManyToOne
    @JoinColumn(name = "id_usuario_responsavel", nullable = true) // Agora pode ser opcional
    private Usuario usuarioResponsavel;

    // Relacionamento com usuários do projeto (M:N)
    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjetoUsuario> usuariosProjeto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();


    public Projeto() {}

    public Projeto(String nome, String descricao, LocalDateTime dataInicio, LocalDateTime dataFim, StatusProjeto status, PrioridadeProjeto prioridade) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.prioridade = prioridade;
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public PrioridadeProjeto getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeProjeto prioridade) {
        this.prioridade = prioridade;
    }

    public Usuario getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(Usuario usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}

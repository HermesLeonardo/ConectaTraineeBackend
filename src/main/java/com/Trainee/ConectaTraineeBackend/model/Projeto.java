package com.Trainee.ConectaTraineeBackend.model;

import com.Trainee.ConectaTraineeBackend.enums.PrioridadeProjeto;
import com.Trainee.ConectaTraineeBackend.enums.StatusProjeto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Nome do projeto é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "Data de início do projeto é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(updatable = false) // Impede que seja alterada manualmente depois de definida
    private LocalDateTime dataFim;

    @NotNull(message = "Status do projeto é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status;

    @NotNull(message = "Prioridade do projeto é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeProjeto prioridade;

    @ManyToOne
    @JoinColumn(name = "id_usuario_responsavel")
    @JsonBackReference  // 🔹 Impede referência circular com Usuario
    private Usuario usuarioResponsavel;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore  // 🔹 Evita carregar lista completa e quebrar a serialização
    private List<ProjetoUsuario> usuariosProjeto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Projeto() {}

    public Projeto(String nome, String descricao, LocalDateTime dataInicio, StatusProjeto status, PrioridadeProjeto prioridade, Usuario usuarioResponsavel) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.status = status;
        this.prioridade = prioridade;
        this.usuarioResponsavel = usuarioResponsavel;
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

    //Lógica para definir automaticamente a data de fim quando o status mudar para "CONCLUIDO"
    public void setStatus(StatusProjeto status) {
        this.status = status;
        if (status == StatusProjeto.CONCLUIDO) {
            this.dataFim = LocalDateTime.now();
        }
    }

    public StatusProjeto getStatus() {
        return status;
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

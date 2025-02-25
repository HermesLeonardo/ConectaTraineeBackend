package com.Trainee.ConectaTraineeBackend.model;

import com.Trainee.ConectaTraineeBackend.enums.PrioridadeProjeto;
import com.Trainee.ConectaTraineeBackend.enums.StatusProjeto;
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

    @Column // 🔹 Agora permite NULL para projetos em andamento
    private LocalDateTime dataFim;

    @NotNull(message = "Status do projeto é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status;

    @NotNull(message = "Prioridade do projeto é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeProjeto prioridade;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();


    @OneToMany(mappedBy = "projeto")
    private List<ProjetoUsuario> projetosUsuarios;



    public Projeto() {}

    public Projeto(String nome, String descricao, LocalDateTime dataInicio, StatusProjeto status, PrioridadeProjeto prioridade) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.status = status;
        this.prioridade = prioridade;
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) {
        this.status = status;
        if (status == StatusProjeto.CONCLUIDO) {
            this.dataFim = LocalDateTime.now();
        }
    }

    public PrioridadeProjeto getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeProjeto prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}

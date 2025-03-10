package com.Trainee.ConectaTraineeBackend.model;

import com.Trainee.ConectaTraineeBackend.enums.PrioridadeProjeto;
import com.Trainee.ConectaTraineeBackend.enums.StatusProjeto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonFormat(pattern = "yyyy-MM-dd") // 🔹 Agora aceita apenas "YYYY-MM-DD"
    private LocalDate  dataInicio;

    @Column // 🔹 Agora permite NULL para projetos em andamento
    @JsonFormat(pattern = "yyyy-MM-dd") // 🔹 Agora aceita apenas "YYYY-MM-DD"
    private LocalDate dataFim;

    @NotNull(message = "Status do projeto é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status;

    @NotNull(message = "Prioridade do projeto é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeProjeto prioridade;

    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // 🔹 Mantém a parte da hora
    private LocalDateTime dataCriacao = LocalDateTime.now();


    @OneToMany(mappedBy = "projeto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ProjetoUsuario> projetosUsuarios = new ArrayList<>();

    @JsonProperty("usuarios")
    public List<Usuario> getUsuarios() {
        if (projetosUsuarios == null || projetosUsuarios.isEmpty()) {
            return new ArrayList<>();
        }
        return projetosUsuarios.stream()
                .map(ProjetoUsuario::getUsuario)
                .distinct()  // 🔹 Evita duplicações
                .collect(Collectors.toList());
    }



    public void setProjetosUsuarios(List<ProjetoUsuario> projetosUsuarios) {
        this.projetosUsuarios.clear(); // Remove os vínculos antigos para evitar duplicação
        if (projetosUsuarios != null) {
            this.projetosUsuarios.addAll(projetosUsuarios);
        }
    }



    public void atualizarUsuarios(List<Usuario> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) {
            this.projetosUsuarios.clear();
            return;
        }

        List<ProjetoUsuario> novosVinculos = usuarios.stream()
                .map(usuario -> new ProjetoUsuario(this, usuario))
                .collect(Collectors.toList());

        this.projetosUsuarios.clear(); // 🔹 Remove vínculos antigos
        this.projetosUsuarios.addAll(novosVinculos);
    }




    public Projeto(List<ProjetoUsuario> projetosUsuarios) {
        this.projetosUsuarios = projetosUsuarios;
    }

    public Projeto(String nome, String descricao, LocalDateTime dataInicio, StatusProjeto status, PrioridadeProjeto prioridade, List<ProjetoUsuario> projetosUsuarios) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = LocalDate.from(dataInicio);
        this.status = status;
        this.prioridade = prioridade;
        this.projetosUsuarios = projetosUsuarios;
        this.dataCriacao = LocalDateTime.now();
    }

    public Projeto() {

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate  getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate  dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate  getDataFim() { return dataFim; }
    public void setDataFim(LocalDate  dataFim) { this.dataFim = dataFim; }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) {
        this.status = status;
        if (status == StatusProjeto.CONCLUIDO) {
            this.dataFim = LocalDate .now();
        }
    }

    public PrioridadeProjeto getPrioridade() { return prioridade; }
    public void setPrioridade(PrioridadeProjeto prioridade) { this.prioridade = prioridade; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setIdUsuarioResponsavel(List<Long> usuariosIds) {}

    @OneToMany(mappedBy = "projeto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Atividade> atividades = new ArrayList<>();

    public List<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades) {
        this.atividades = atividades;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario_responsavel", nullable = true)
    private Usuario usuarioResponsavel;

    public Usuario getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(Usuario usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

}

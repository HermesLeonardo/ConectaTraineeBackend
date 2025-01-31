package com.Trainee.ConectaTraineeBackend.model;

import com.Trainee.ConectaTraineeBackend.enums.StatusAtividade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "atividades")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @NotNull(message = "Nome da atividade é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataInicio = LocalDateTime.now(); // ✅ Definindo automaticamente

    @Column
    private LocalDateTime dataFim; // 🔥 Agora é opcional e será preenchido quando a atividade for concluída

    @NotNull(message = "Status da atividade é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtividade status;

    @ManyToOne
    @JoinColumn(name = "id_usuario_responsavel", nullable = false)
    private Usuario usuarioResponsavel;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // ✅ Construtor vazio (necessário para o JPA)
    public Atividade() {}

    // ✅ Construtor principal
    public Atividade(Projeto projeto, String nome, String descricao, StatusAtividade status, Usuario usuarioResponsavel) {
        this.projeto = projeto;
        this.nome = nome;
        this.descricao = descricao;
        this.status = status;
        this.usuarioResponsavel = usuarioResponsavel;
        this.dataCriacao = LocalDateTime.now();
        this.dataInicio = LocalDateTime.now(); // ✅ Definindo automaticamente
    }

    // ✅ Métodos Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
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

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public StatusAtividade getStatus() {
        return status;
    }

    public void setStatus(StatusAtividade status) {
        this.status = status;
        if (status == StatusAtividade.CONCLUIDA) {
            this.dataFim = LocalDateTime.now(); //Definindo automaticamente
        }
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

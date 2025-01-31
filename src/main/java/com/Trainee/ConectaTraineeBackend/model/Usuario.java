package com.Trainee.ConectaTraineeBackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import validator.constraints.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @notNull(message = "Nome é obrigatório")
    @size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    @Column(nullable = false) 
    private String nome;

    @notNull(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(unique = true, nullable = false)
    private String email;

    @notNull(message = "Senha é obrigatório")
    @size(min = 6, max = 50, message = "Senha deve ter entre 6 e 50 caracteres")
    @Column(nullable = false)
    private String senha; // Aqui armazenar como hash futuramente

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @Column(nullable = false)
    private String perfil; // Ex.: "ADMIN" ou "USUARIO"

    // Relacionamento com os projetos (M:N)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjetoUsuario> projetosUsuario;

    
    public Usuario() {
    }

    public Usuario(String nome, String email, String senha, String perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.dataCriacao = LocalDateTime.now(); // Inicia como a data atual
    }


    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }
}

package com.Trainee.ConectaTraineeBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.Trainee.ConectaTraineeBackend.model.Projeto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    Optional<Projeto> findByNome(String nome);

    @Query("SELECT p FROM Projeto p " +
            "LEFT JOIN FETCH p.usuarioResponsavel " +
            "LEFT JOIN FETCH p.projetosUsuarios pu " +
            "LEFT JOIN FETCH pu.usuario " +
            "WHERE p.id = :id")
    Optional<Projeto> buscarProjetoComUsuarios(@Param("id") Long id);

    @Query("SELECT p FROM Projeto p " +
            "JOIN ProjetoUsuario pu ON p.id = pu.projeto.id " +
            "WHERE pu.usuario.id = :usuarioId")
    List<Projeto> buscarProjetosDoUsuario(@Param("usuarioId") Long usuarioId);


}
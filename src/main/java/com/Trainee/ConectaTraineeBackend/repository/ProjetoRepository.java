package com.Trainee.ConectaTraineeBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.Trainee.ConectaTraineeBackend.model.Projeto;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    Optional<Projeto> findByNome(String nome);

    @Query("SELECT p FROM Projeto p " +
            "JOIN FETCH p.projetosUsuarios pu " +
            "JOIN FETCH pu.usuario " +
            "WHERE p.id = :id")
    Optional<Projeto> buscarProjetoComUsuarios(@Param("id") Long id);

}

package com.Trainee.ConectaTraineeBackend.repository;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    List<Atividade> findByProjetoId(Long idProjeto);

    List<Atividade> findByUsuariosResponsaveis_Id(Long idUsuario);

    @Query("SELECT a FROM Atividade a LEFT JOIN FETCH a.usuariosResponsaveis WHERE a.id = :id")
    Optional<Atividade> findByIdWithUsuarios(@Param("id") Long id);

    @Query("SELECT a FROM Atividade a " +
            "JOIN a.usuariosResponsaveis u " +
            "WHERE u.id = :idUsuario")
    List<Atividade> buscarAtividadesDoUsuario(@Param("idUsuario") Long idUsuario);


    @Query("SELECT a FROM Atividade a JOIN a.usuariosResponsaveis u WHERE u.id = :idUsuario")
    List<Atividade> findAtividadesByUsuario(@Param("idUsuario") Long idUsuario);

    List<Atividade> findByAtivoTrue();

}

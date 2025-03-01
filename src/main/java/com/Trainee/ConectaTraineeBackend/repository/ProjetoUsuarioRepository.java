package com.Trainee.ConectaTraineeBackend.repository;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjetoUsuarioRepository extends JpaRepository<ProjetoUsuario, Long> {

    List<ProjetoUsuario> findByUsuarioId(Long idUsuario);

    List<ProjetoUsuario> findByProjetoId(Long idProjeto);

    // Remove todos os vínculos de um projeto específico
    void deleteByProjeto(Projeto projeto);

    // Adicionando @Query para melhorar a performance
    boolean existsByUsuarioId(Long id);
}

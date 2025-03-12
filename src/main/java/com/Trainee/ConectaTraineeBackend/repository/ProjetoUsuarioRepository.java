package com.Trainee.ConectaTraineeBackend.repository;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProjetoUsuarioRepository extends JpaRepository<ProjetoUsuario, Long> {

    List<ProjetoUsuario> findByUsuarioId(Long idUsuario);

    List<ProjetoUsuario> findByProjetoId(Long idProjeto);

    // Remove todos os vínculos de um projeto específico
    void deleteByProjeto(Projeto projeto);

    // Adicionando @Query para melhorar a performance
    boolean existsByUsuarioId(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM ProjetoUsuario pu WHERE pu.projeto.id = :projetoId")
    void deleteByProjetoId(Long projetoId);

    boolean existsByProjetoIdAndUsuarioId(Long projetoId, Long usuarioId);

    // Busca apenas os IDs dos usuários vinculados a um projeto
    @Query("SELECT pu.usuario.id FROM ProjetoUsuario pu WHERE pu.projeto.id = :projetoId")
    List<Long> findUsuariosIdsByProjetoId(Long projetoId);
}

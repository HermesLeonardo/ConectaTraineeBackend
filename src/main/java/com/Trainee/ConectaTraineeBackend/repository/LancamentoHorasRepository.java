package com.Trainee.ConectaTraineeBackend.repository;

import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface LancamentoHorasRepository extends JpaRepository<LancamentoHoras, Long> {


    List<LancamentoHoras> findByAtividadeId(Long idAtividade);

    boolean existsByUsuarioId(Long id);

    List<LancamentoHoras> findByUsuarioId(Long usuarioId);

    List<LancamentoHoras> findByCanceladoFalse(); // 🔹 Apenas lançamentos NÃO cancelados



    @Query("SELECT l FROM LancamentoHoras l WHERE l.usuario.id = :usuarioId AND l.cancelado = false ORDER BY l.dataInicio DESC")
    Page<LancamentoHoras> buscarUltimosLancamentosPorUsuario(@Param("usuarioId") Long usuarioId, Pageable pageable);





}

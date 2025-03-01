package com.Trainee.ConectaTraineeBackend.repository;

import com.Trainee.ConectaTraineeBackend.model.LancamentoHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LancamentoHorasRepository extends JpaRepository<LancamentoHoras, Long> {

    List<LancamentoHoras> findByAtividadeId(Long idAtividade);

    // Melhoria: Adicionar @Query para eficiência
    boolean existsByUsuarioId(Long id);
}

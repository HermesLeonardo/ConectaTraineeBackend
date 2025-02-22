package com.Trainee.ConectaTraineeBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Trainee.ConectaTraineeBackend.model.AtividadeUsuario;
@Repository
public interface AtividadeUsuarioRepository extends JpaRepository<AtividadeUsuario, Long> {
}

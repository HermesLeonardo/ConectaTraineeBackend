package com.Trainee.ConectaTraineeBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Trainee.ConectaTraineeBackend.model.Projetos;
import java.util.Optional;
@Repository
public interface ProjetosRepository extends JpaRepository<Projetos, Long> {
    Optional<Projetos> findByNome(String nome);
}

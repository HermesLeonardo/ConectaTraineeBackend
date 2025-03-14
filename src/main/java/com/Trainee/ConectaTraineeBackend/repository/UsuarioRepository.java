package com.Trainee.ConectaTraineeBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByPerfil(String admin);

    List<Usuario> findAllById(Iterable<Long> ids);

    List<Usuario> findByAtivoTrue();
    List<Usuario> findByAtivoFalse();

    boolean existsByEmail(String email);
}

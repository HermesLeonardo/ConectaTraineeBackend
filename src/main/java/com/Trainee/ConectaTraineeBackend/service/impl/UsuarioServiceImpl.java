package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        logger.info("Salvando usuário com email: {}", usuario.getEmail());
        try {
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            logger.error("Erro ao salvar usuário: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        logger.info("Buscando usuário com ID: {}", id);
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        logger.info("Listando todos os usuários cadastrados.");
        return usuarioRepository.findAll();
    }

    @Override
    public void deletarUsuario(Long id) {
        logger.info("Deletando usuário com ID: {}", id);
        usuarioRepository.deleteById(id);
    }
}

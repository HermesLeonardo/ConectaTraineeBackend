package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        logger.info("🔐 Verificando necessidade de criptografar a senha...");

        // Evita criptografar senhas já criptografadas
        if (!usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        logger.info("✅ Senha tratada e usuário salvo: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
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
        logger.warn("⚠ Tentativa de exclusão do usuário ID: {}", id);

        Optional<Usuario> usuario = usuarioRepository.findById(id);
        if (usuario.isPresent()) {
            logger.warn("⚠ Usuário encontrado: {}", usuario.get().getEmail());
        } else {
            logger.error("❌ ERRO: Usuário já não existe antes da exclusão!");
        }

        usuarioRepository.deleteById(id);
        logger.info("✅ Usuário deletado com sucesso!");
    }


    public void desativarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
            logger.info("✅ Usuário {} desativado com sucesso!", usuario.getEmail());
        } else {
            logger.warn("❌ Tentativa de desativar um usuário que não existe: ID {}", id);
        }
    }




    @Override
    public Set<Atividade> listarAtividades(Long id) {
        return null;
    }
}

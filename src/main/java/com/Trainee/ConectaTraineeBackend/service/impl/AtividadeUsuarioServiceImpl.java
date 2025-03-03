package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.AtividadeRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeUsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AtividadeUsuarioServiceImpl implements AtividadeUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(AtividadeUsuarioServiceImpl.class);

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Atividade adicionarUsuarios(Long idAtividade, Set<Usuario> usuarios) {
        logger.info("🔄 Adicionando usuários à atividade ID: {}", idAtividade);

        Optional<Atividade> atividadeOptional = atividadeRepository.findById(idAtividade);
        if (atividadeOptional.isEmpty()) {
            logger.error("❌ Atividade ID {} não encontrada!", idAtividade);
            throw new RuntimeException("Atividade não encontrada!");
        }

        Atividade atividade = atividadeOptional.get();
        atividade.getUsuariosResponsaveis().addAll(usuarios);

        // 🔹 Garante que os usuários sejam salvos corretamente na tabela `atividades_usuarios`
        Atividade atividadeSalva = atividadeRepository.save(atividade);

        logger.info("✅ Usuários adicionados à atividade '{}': {} usuários vinculados.",
                atividade.getNome(), atividadeSalva.getUsuariosResponsaveis().size());

        return atividadeSalva;
    }

    @Override
    public Atividade removerUsuario(Long idAtividade, Long idUsuario) {
        logger.info("🔄 Removendo usuário ID {} da atividade ID {}", idUsuario, idAtividade);

        Optional<Atividade> atividadeOptional = atividadeRepository.findById(idAtividade);
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);

        if (atividadeOptional.isEmpty() || usuarioOptional.isEmpty()) {
            logger.error("❌ Atividade ID {} ou Usuário ID {} não encontrado!", idAtividade, idUsuario);
            throw new RuntimeException("Atividade ou usuário não encontrado!");
        }

        Atividade atividade = atividadeOptional.get();
        Usuario usuario = usuarioOptional.get();
        atividade.getUsuariosResponsaveis().remove(usuario);

        Atividade atividadeAtualizada = atividadeRepository.save(atividade);
        logger.info("✅ Usuário ID {} removido da atividade '{}'", idUsuario, atividade.getNome());

        return atividadeAtualizada;
    }

    @Override
    public Set<Usuario> listarUsuarios(Long idAtividade) {
        logger.info("📌 Listando usuários responsáveis pela atividade ID {}", idAtividade);

        Optional<Atividade> atividadeOptional = atividadeRepository.findById(idAtividade);
        if (atividadeOptional.isEmpty()) {
            logger.error("❌ Atividade ID {} não encontrada!", idAtividade);
            throw new RuntimeException("Atividade não encontrada!");
        }

        Set<Usuario> usuarios = atividadeOptional.get().getUsuariosResponsaveis();
        logger.info("✅ {} usuários encontrados na atividade ID {}", usuarios.size(), idAtividade);

        return usuarios;
    }
}

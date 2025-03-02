package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.AtividadeRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AtividadeServiceImpl implements AtividadeService {

    private static final Logger logger = LoggerFactory.getLogger(AtividadeServiceImpl.class);

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Atividade salvarAtividade(Atividade atividade, Set<Long> usuariosIds) {
        logger.info("Salvando atividade: {}", atividade.getNome());

        if (usuariosIds != null && !usuariosIds.isEmpty()) {
            Set<Usuario> usuarios = new HashSet<>(usuarioRepository.findAllById(usuariosIds));

            if (!usuarios.isEmpty()) {
                atividade.setUsuariosResponsaveis(usuarios);
            } else {
                logger.warn("Nenhum usuário encontrado para os IDs: {}", usuariosIds);
                atividade.setUsuariosResponsaveis(new HashSet<>()); // Garante que não seja nulo
            }
        } else {
            logger.warn("Nenhum usuário foi passado para a atividade {}", atividade.getNome());
            atividade.setUsuariosResponsaveis(new HashSet<>()); // Garante que não seja nulo
        }


        // 🔹 Garante que `usuariosResponsaveis` nunca seja null antes de salvar
        if (atividade.getUsuariosResponsaveis() == null) {
            atividade.setUsuariosResponsaveis(new HashSet<>());
        }

        Atividade atividadeSalva = atividadeRepository.save(atividade);

        logger.info("Usuários vinculados: {}", atividadeSalva.getUsuariosResponsaveis().size());

        return atividadeSalva;
    }




    @Override
    public Optional<Atividade> buscarPorId(Long id) {
        logger.info("Buscando atividade com ID: {}", id);
        return atividadeRepository.findByIdWithUsuarios(id);
    }


    @Override
    public List<Atividade> listarTodos() {
        logger.info("Listando todas as atividades cadastradas.");
        return atividadeRepository.findAll();
    }

    @Override
    public void deletarAtividade(Long id) {
        logger.info("Deletando atividade com ID: {}", id);
        try {
            atividadeRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Erro ao deletar atividade com ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Erro ao deletar atividade", e);
        }
    }

    @Override
    public List<Atividade> buscarPorProjeto(Long idProjeto) {
        logger.info("Buscando atividades do projeto com ID: {}", idProjeto);
        return atividadeRepository.findByProjetoId(idProjeto);
    }

    @Override
    public Atividade adicionarUsuarios(Long id, Set<Usuario> usuarios) {
        Optional<Atividade> atividadeOpt = atividadeRepository.findById(id);
        if (atividadeOpt.isEmpty()) {
            throw new RuntimeException("Atividade não encontrada");
        }
        Atividade atividade = atividadeOpt.get();
        atividade.getUsuariosResponsaveis().addAll(usuarios);
        return atividadeRepository.save(atividade);
    }

    @Override
    public Atividade removerUsuario(Long id, Long idUsuario) {
        Optional<Atividade> atividadeOpt = atividadeRepository.findById(id);
        if (atividadeOpt.isEmpty()) {
            throw new RuntimeException("Atividade não encontrada");
        }
        Atividade atividade = atividadeOpt.get();
        atividade.getUsuariosResponsaveis().removeIf(usuario -> usuario.getId().equals(idUsuario));
        return atividadeRepository.save(atividade);
    }

    @Override
    public Set<Usuario> listarUsuarios(Long id) {
        Optional<Atividade> atividadeOpt = atividadeRepository.findById(id);
        if (atividadeOpt.isEmpty()) {
            throw new RuntimeException("Atividade não encontrada");
        }

        return atividadeOpt.get().getUsuariosResponsaveis() != null ?
                atividadeOpt.get().getUsuariosResponsaveis() :
                new HashSet<>();
    }


}

package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.AtividadeRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        logger.info("🔹 Salvando atividade: {}", atividade.getNome());
        logger.info("🔑 Token recebido: {}", SecurityContextHolder.getContext().getAuthentication());


        if (usuariosIds != null && !usuariosIds.isEmpty()) {
            Set<Usuario> usuarios = new HashSet<>(usuarioRepository.findAllById(usuariosIds));
            atividade.setUsuariosResponsaveis(usuarios);
            logger.info("✅ {} usuários encontrados e vinculados.", usuarios.size());
        } else {
            logger.warn("⚠ Nenhum usuário foi passado para a atividade {}", atividade.getNome());
            atividade.setUsuariosResponsaveis(new HashSet<>());
        }

        Atividade atividadeSalva = atividadeRepository.save(atividade);

        if (!atividadeSalva.getUsuariosResponsaveis().isEmpty()) {
            atividadeSalva.getUsuariosResponsaveis().forEach(usuario ->
                    logger.info("✅ Usuário {} vinculado à atividade {}", usuario.getId(), atividade.getNome()));
        } else {
            logger.warn("⚠ Nenhum usuário salvo na atividade {}", atividade.getNome());
        }

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
    @Transactional
    public void deletarAtividade(Long id) {
        if (atividadeRepository.existsById(id)) {
            atividadeRepository.deleteById(id);
            atividadeRepository.flush(); // 🔹 Garante a persistência no banco
        } else {
            throw new EntityNotFoundException("Atividade não encontrada para exclusão: ID " + id);
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

    @Override
    public List<Atividade> buscarAtividadesPorUsuario(Long idUsuario) {
        logger.info("🔍 Buscando atividades vinculadas ao usuário ID: {}", idUsuario);
        return atividadeRepository.findAtividadesByUsuario(idUsuario);
    }


    @Transactional
    public void desativarAtividade(Long id) {
        Optional<Atividade> atividade = atividadeRepository.findById(id);

        if (atividade.isPresent()) {
            Atividade atividadeDesativada = atividade.get();
            atividadeDesativada.setAtivo(false); // 🔹 Desativando a atividade
            atividadeRepository.save(atividadeDesativada);
        } else {
            throw new EntityNotFoundException("Atividade não encontrada para desativação: ID " + id);
        }
    }

    public List<Atividade> listarAtividades() {
        return atividadeRepository.findByAtivoTrue();
    }





}

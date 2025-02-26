package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoUsuarioRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.ProjetoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjetoServiceImpl implements ProjetoService {

    private static final Logger logger = LoggerFactory.getLogger(ProjetoServiceImpl.class);

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProjetoUsuarioRepository projetoUsuarioRepository;

    @Override
    public Projeto salvarProjeto(Projeto projeto, List<Long> usuariosIds) {
        logger.info("💾 Salvando projeto: {}", projeto.getNome());

        Projeto projetoSalvo = projetoRepository.save(projeto);
        logger.info("✅ Projeto salvo no banco com ID {}", projetoSalvo.getId());

        // Verifica se há usuários para vincular ao projeto
        if (usuariosIds != null && !usuariosIds.isEmpty()) {
            for (Long idUsuario : usuariosIds) {
                Usuario usuario = usuarioRepository.findById(idUsuario)
                        .orElseThrow(() -> new RuntimeException("Usuário com ID " + idUsuario + " não encontrado"));

                ProjetoUsuario projetoUsuario = new ProjetoUsuario(projetoSalvo, usuario);
                projetoUsuarioRepository.save(projetoUsuario);
                logger.info("✅ Usuário {} vinculado ao projeto {}", usuario.getNome(), projetoSalvo.getNome());
            }
        } else {
            logger.warn("⚠ Nenhum usuário foi vinculado ao projeto.");
        }

        return projetoSalvo;
    }





    @Override
    public Optional<Projeto> buscarPorId(Long id) {
        logger.info("Buscando projeto com ID: {}", id);
        Optional<Projeto> projetoOpt = projetoRepository.findById(id);

        if (projetoOpt.isPresent()) {
            Projeto projeto = projetoOpt.get();

            // Buscar os IDs dos usuários responsáveis pelo projeto
            List<Long> usuariosIds = projetoUsuarioRepository.findByProjetoId(id)
                    .stream()
                    .map(projetoUsuario -> projetoUsuario.getUsuario().getId())
                    .collect(Collectors.toList());

            projeto.setIdUsuarioResponsavel(usuariosIds);
            return Optional.of(projeto);
        }

        return Optional.empty();
    }


    @Override
    public List<Projeto> listarTodos() {
        logger.info("Listando todos os projetos cadastrados.");
        return projetoRepository.findAll();
    }

    @Override
    public void deletarProjeto(Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetoRepository.deleteById(id);
    }

    public Projeto atualizarProjeto(Long id, Projeto projeto) {
        Projeto projetoExistente = projetoRepository.findById(id).orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        projetoExistente.setNome(projeto.getNome());
        projetoExistente.setDescricao(projeto.getDescricao());
        projetoExistente.setStatus(projeto.getStatus());
        projetoExistente.setPrioridade(projeto.getPrioridade());
        return projetoRepository.save(projetoExistente);
    }


}

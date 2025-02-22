package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.AtividadeRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AtividadeServiceImpl implements AtividadeService {

    private static final Logger logger = LoggerFactory.getLogger(AtividadeServiceImpl.class);

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Override
    public Atividade salvarAtividade(Atividade atividade) {
        logger.info("Salvando atividade: {}", atividade.getNome());
        try {
            return atividadeRepository.save(atividade);
        } catch (Exception e) {
            logger.error("Erro ao salvar atividade: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar atividade", e);
        }
    }

    @Override
    public Optional<Atividade> buscarPorId(Long id) {
        logger.info("Buscando atividade com ID: {}", id);
        return atividadeRepository.findById(id);
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
        return null;
    }

    @Override
    public Atividade removerUsuario(Long id, Long idUsuario) {
        return null;
    }

    @Override
    public Set<Usuario> listarUsuarios(Long id) {
        return null;
    }
}

package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Projetos;
import com.Trainee.ConectaTraineeBackend.repository.ProjetosRepository;
import com.Trainee.ConectaTraineeBackend.service.ProjetosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetosServiceImpl implements ProjetosService {

    private static final Logger logger = LoggerFactory.getLogger(ProjetosServiceImpl.class);

    @Autowired
    private ProjetosRepository projetosRepository;

    @Override
    public Projetos salvarProjeto(Projetos projetos) {
        logger.info("Salvando projeto: {}", projetos.getNome());
        try {
            return projetosRepository.save(projetos);
        } catch (Exception e) {
            logger.error("Erro ao salvar projeto: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar projeto", e);
        }
    }

    @Override
    public Optional<Projetos> buscarPorId(Long id) {
        logger.info("Buscando projeto com ID: {}", id);
        return projetosRepository.findById(id);
    }

    @Override
    public List<Projetos> listarTodos() {
        logger.info("Listando todos os projetos cadastrados.");
        return projetosRepository.findAll();
    }

    @Override
    public void deletarProjeto(Long id) {
        logger.info("Deletando projeto com ID: {}", id);
        projetosRepository.deleteById(id);
    }
}

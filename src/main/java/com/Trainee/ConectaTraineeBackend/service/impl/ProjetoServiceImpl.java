package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.service.ProjetoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProjetoServiceImpl implements ProjetoService {

    private static final Logger logger = LoggerFactory.getLogger(ProjetoServiceImpl.class);

    @Autowired
    private ProjetoRepository projetoRepository;

    @Override
    public Projeto salvarProjeto(Projeto projeto) {
        logger.info("Salvando projeto: {}", projeto.getNome());
        try {
            return projetoRepository.save(projeto);
        } catch (Exception e) {
            logger.error("Erro ao salvar projeto: {}", e.getMessage());
            throw new RuntimeException("Erro ao salvar projeto", e);
        }
    }

    @Override
    public Optional<Projeto> buscarPorId(Long id) {
        logger.info("Buscando projeto com ID: {}", id);
        return projetoRepository.findById(id);
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
}

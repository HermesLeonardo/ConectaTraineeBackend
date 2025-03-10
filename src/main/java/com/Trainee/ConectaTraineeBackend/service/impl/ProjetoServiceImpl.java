package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoUsuarioRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.ProjetoService;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Projeto salvarProjeto(Projeto projeto, List<Long> usuariosIds) {
        return salvarProjeto(projeto, usuariosIds, null); // Chama a nova versão do método sem definir responsável
    }


    @Override
    @Transactional
    public Projeto salvarProjeto(Projeto projeto, List<Long> usuariosIds, Long idUsuarioResponsavel) {
        logger.info("💾 Salvando projeto: {}", projeto.getNome());

        // 🔹 Se foi passado um ID de responsável, buscamos e verificamos se ele é ADMIN
        if (idUsuarioResponsavel != null) {
            Usuario usuarioResponsavel = usuarioRepository.findById(idUsuarioResponsavel)
                    .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado"));

            if (!"ADMIN".equals(usuarioResponsavel.getPerfil())) {
                throw new RuntimeException("Somente usuários ADMIN podem ser responsáveis por projetos!");
            }

            projeto.setUsuarioResponsavel(usuarioResponsavel); // ✅ Define corretamente o responsável
            logger.info("✅ Usuário responsável atribuído: {} (ID: {})", usuarioResponsavel.getNome(), usuarioResponsavel.getId());
        } else {
            projeto.setUsuarioResponsavel(null); // ✅ Evita inconsistências ao remover o responsável
            logger.warn("⚠ Nenhum usuário responsável foi atribuído!");
        }

        // 🔹 Salva o projeto no banco
        Projeto projetoSalvo = projetoRepository.save(projeto);
        logger.info("✅ Projeto salvo no banco com ID {}", projetoSalvo.getId());

        // 🔹 Removemos todos os vínculos anteriores para evitar duplicações
        projetoUsuarioRepository.deleteByProjeto(projetoSalvo);
        logger.info("🗑️ Vínculos antigos removidos para atualização dos usuários");

        // 🔹 Verifica se há novos usuários para vincular
        if (usuariosIds != null && !usuariosIds.isEmpty()) {
            List<Usuario> usuarios = usuarioRepository.findAllById(usuariosIds);

            for (Usuario usuario : usuarios) {
                ProjetoUsuario projetoUsuario = new ProjetoUsuario(projetoSalvo, usuario);
                projetoUsuarioRepository.save(projetoUsuario);
                logger.info("✅ Usuário {} vinculado ao projeto {}", usuario.getNome(), projetoSalvo.getNome());
            }
        } else {
            logger.warn("⚠ Nenhum usuário foi vinculado ao projeto.");
        }

        // 🔹 Garante que o usuário responsável seja salvo corretamente no banco
        projetoSalvo = projetoRepository.findById(projetoSalvo.getId()).orElse(projetoSalvo);
        if (projetoSalvo.getUsuarioResponsavel() != null) {
            logger.info("📌 ID do usuário responsável salvo corretamente: {}", projetoSalvo.getUsuarioResponsavel().getId());
        } else {
            logger.warn("⚠ O usuário responsável ainda está NULL após a persistência!");
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

    @Override
    public Optional<Projeto> buscarProjetoComUsuarios(Long id) {
        return projetoRepository.buscarProjetoComUsuarios(id);
    }

    @Override
    @Transactional
    public Projeto atualizarProjeto(Long id, Projeto projetoAtualizado, List<Long> usuariosIds, Long responsavelId) {
        logger.info("📝 Atualizando projeto com ID {}", id);

        // 🔹 Verifica se o projeto existe no banco de dados
        Projeto projetoExistente = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        // 🔹 Atualiza os atributos principais do projeto
        projetoExistente.setNome(projetoAtualizado.getNome());
        projetoExistente.setDescricao(projetoAtualizado.getDescricao());
        projetoExistente.setStatus(projetoAtualizado.getStatus());
        projetoExistente.setPrioridade(projetoAtualizado.getPrioridade());
        projetoExistente.setDataInicio(projetoAtualizado.getDataInicio());
        projetoExistente.setDataFim(projetoAtualizado.getDataFim());

        // 🔹 Atualiza o usuário responsável pelo projeto (caso tenha sido fornecido)
        if (responsavelId != null) {
            Usuario usuarioResponsavel = usuarioRepository.findById(responsavelId)
                    .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado"));

            if (!"ADMIN".equals(usuarioResponsavel.getPerfil())) {
                throw new RuntimeException("Somente usuários ADMIN podem ser responsáveis por projetos!");
            }

            projetoExistente.setUsuarioResponsavel(usuarioResponsavel);
            logger.info("✅ Usuário responsável atualizado para: {} (ID: {})", usuarioResponsavel.getNome(), usuarioResponsavel.getId());
        } else {
            projetoExistente.setUsuarioResponsavel(null);
            logger.warn("⚠ Nenhum usuário responsável foi definido!");
        }

        // 🔹 Atualiza os usuários vinculados ao projeto
        if (usuariosIds != null && !usuariosIds.isEmpty()) {
            List<Usuario> usuarios = usuarioRepository.findAllById(usuariosIds);
            projetoExistente.atualizarUsuarios(usuarios); // ✅ Atualiza os usuários vinculados

            // 🔹 Remove vínculos antigos e adiciona os novos
            projetoUsuarioRepository.deleteByProjeto(projetoExistente);
            List<ProjetoUsuario> novosVinculos = usuarios.stream()
                    .map(usuario -> new ProjetoUsuario(projetoExistente, usuario))
                    .collect(Collectors.toList());

            projetoUsuarioRepository.saveAll(novosVinculos);
            logger.info("✅ {} usuários vinculados ao projeto {}", novosVinculos.size(), projetoExistente.getNome());
        } else {
            logger.warn("⚠ Nenhum usuário foi vinculado ao projeto.");
        }

        // 🔹 Salva as alterações no banco de dados
        projetoRepository.save(projetoExistente);

        // 🔹 Verificação final para garantir que o responsável foi persistido corretamente
        if (projetoExistente.getUsuarioResponsavel() != null) {
            logger.info("📌 Confirmação final do usuário responsável salvo: {}",
                    projetoExistente.getUsuarioResponsavel().getId());
        } else {
            logger.warn("⚠ O usuário responsável ainda está NULL após a persistência!");
        }

        return projetoExistente;
    }







}

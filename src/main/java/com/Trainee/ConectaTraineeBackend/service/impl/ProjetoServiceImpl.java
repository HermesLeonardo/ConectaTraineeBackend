package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.DTO.Dtos.ProjetoDTO;
import com.Trainee.ConectaTraineeBackend.model.*;
import com.Trainee.ConectaTraineeBackend.repository.*;
import com.Trainee.ConectaTraineeBackend.service.ProjetoService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Autowired
    private LancamentoHorasRepository lancamentoHorasRepository;


    @Autowired
    public ProjetoServiceImpl(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }

    @Override
    public List<Projeto> listarProjetosDoUsuario(Long usuarioId) {
        return projetoRepository.buscarProjetosDoUsuario(usuarioId);
    }

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
                ProjetoUsuario projetoUsuario = new ProjetoUsuario(projeto, usuario);
                projetoUsuarioRepository.save(projetoUsuario);
                logger.info("✅ Usuário {} vinculado ao projeto {}", usuario.getNome(), projeto.getNome());
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


    @Autowired
    private AtividadeRepository atividadeRepository;


    @Override
    public Optional<Projeto> buscarPorId(Long id) {
        logger.info("🔍 Buscando projeto com ID: {}", id);

        // Buscar o projeto com usuários e atividades diretamente
        Optional<Projeto> projetoOpt = projetoRepository.buscarProjetoComAtividades(id);

        if (projetoOpt.isPresent()) {
            Projeto projeto = projetoOpt.get();

            // Buscar os IDs dos usuários vinculados ao projeto
            List<Long> usuariosIds = projetoUsuarioRepository.findByProjetoId(id)
                    .stream()
                    .map(projetoUsuario -> projetoUsuario.getUsuario().getId())
                    .collect(Collectors.toList());

            projeto.setIdUsuarioResponsavel(usuariosIds);

            // Buscar as atividades associadas ao projeto
            List<Atividade> atividades = atividadeRepository.findByProjetoId(id);
            projeto.setAtividades(atividades);

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
        logger.info("📝 Atualizando projeto ID: {}", id);

        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        // 🔹 Atualiza os atributos básicos
        projeto.setNome(projetoAtualizado.getNome());
        projeto.setDescricao(projetoAtualizado.getDescricao());
        projeto.setStatus(projetoAtualizado.getStatus());
        projeto.setPrioridade(projetoAtualizado.getPrioridade());
        projeto.setDataInicio(projetoAtualizado.getDataInicio());
        projeto.setDataFim(projetoAtualizado.getDataFim());

        // 🔹 Remove todos os vínculos antigos antes de salvar os novos
        projetoUsuarioRepository.deleteByProjetoId(projeto.getId());

        // 🔹 Atualiza o usuário responsável
        if (responsavelId != null) {
            Usuario responsavel = usuarioRepository.findById(responsavelId)
                    .orElseThrow(() -> new RuntimeException("Usuário responsável não encontrado"));

            if (!"ADMIN".equals(responsavel.getPerfil())) {
                throw new RuntimeException("Somente usuários ADMIN podem ser responsáveis por projetos!");
            }

            projeto.setUsuarioResponsavel(responsavel);
            logger.info("✅ Responsável atualizado: {} (ID: {})", responsavel.getNome(), responsavel.getId());
        } else {
            projeto.setUsuarioResponsavel(null);
            logger.warn("⚠ Nenhum usuário responsável foi definido.");
        }

        // 🔹 Atualiza os usuários vinculados ao projeto
        if (usuariosIds != null && !usuariosIds.isEmpty()) {
            List<Usuario> usuarios = usuarioRepository.findAllById(usuariosIds);

            // ✅ Remove duplicatas
            Set<Long> usuariosExistentes = new HashSet<>(projetoUsuarioRepository.findUsuariosIdsByProjetoId(projeto.getId()));
            List<ProjetoUsuario> novosVinculos = usuarios.stream()
                    .filter(usuario -> !usuariosExistentes.contains(usuario.getId()))
                    .map(usuario -> new ProjetoUsuario(projeto, usuario))
                    .collect(Collectors.toList());

            projetoUsuarioRepository.saveAll(novosVinculos);
            logger.info("✅ {} usuários vinculados ao projeto {}", novosVinculos.size(), projeto.getNome());
        } else {
            logger.warn("⚠ Nenhum usuário foi vinculado ao projeto.");
        }

        return projetoRepository.save(projeto);
    }



    @Override
    public double calcularTotalHorasLancadas() {
        List<LancamentoHoras> lancamentos = lancamentoHorasRepository.findAll();

        return lancamentos.stream()
                .mapToDouble(lanc -> {
                    if (lanc.getDataInicio() == null || lanc.getDataFim() == null) {
                        return 0; // Evita erro se os dados estiverem incompletos
                    }
                    Duration duracao = Duration.between(lanc.getDataInicio(), lanc.getDataFim());
                    return duracao.toHours(); // Converte a duração para horas
                })
                .sum();
    }


    public ProjetoDTO buscarProjetoComUsuariosEAtividades(Long id) {
        Projeto projeto = projetoRepository.findByIdWithUsersAndActivities(id);
        return new ProjetoDTO(projeto);
    }

    @Override
    public List<Projeto> listarProjetosComDetalhes() {
        return projetoRepository.findTodosProjetosComDetalhes();
    }




}

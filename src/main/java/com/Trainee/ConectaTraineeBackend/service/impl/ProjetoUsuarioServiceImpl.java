package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Projeto;
import com.Trainee.ConectaTraineeBackend.model.ProjetoUsuario;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoUsuarioRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.ProjetoUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjetoUsuarioServiceImpl implements ProjetoUsuarioService {

    @Autowired
    private ProjetoUsuarioRepository projetoUsuarioRepository;

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public ProjetoUsuario adicionarUsuarioAoProjeto(Long idProjeto, Long idUsuario) {
        Projeto projeto = projetoRepository.findById(idProjeto)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ProjetoUsuario projetoUsuario = new ProjetoUsuario(projeto, usuario);
        return projetoUsuarioRepository.save(projetoUsuario);
    }

    @Override
    public List<ProjetoUsuario> listarProjetosPorUsuario(Long idUsuario) {
        return projetoUsuarioRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<ProjetoUsuario> listarUsuariosPorProjeto(Long idProjeto) {
        return projetoUsuarioRepository.findByProjetoId(idProjeto);
    }
}

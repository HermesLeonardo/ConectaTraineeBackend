package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.AtividadeRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.AtividadeUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AtividadeUsuarioServiceImpl implements AtividadeUsuarioService {

    @Autowired
    private AtividadeRepository atividadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Atividade adicionarUsuarios(Long idAtividade, Set<Usuario> usuarios) {
        Optional<Atividade> atividadeOptional = atividadeRepository.findById(idAtividade);
        if (atividadeOptional.isPresent()) {
            Atividade atividade = atividadeOptional.get();
            atividade.getUsuariosResponsaveis().addAll(usuarios);
            return atividadeRepository.save(atividade);
        }
        throw new RuntimeException("Atividade não encontrada!");
    }


    @Override
    public Atividade removerUsuario(Long idAtividade, Long idUsuario) {
        Optional<Atividade> atividadeOptional = atividadeRepository.findById(idAtividade);
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);

        if (atividadeOptional.isPresent() && usuarioOptional.isPresent()) {
            Atividade atividade = atividadeOptional.get();
            Usuario usuario = usuarioOptional.get();
            atividade.getUsuariosResponsaveis().remove(usuario);
            return atividadeRepository.save(atividade);
        }
        throw new RuntimeException("Atividade ou usuário não encontrado!");
    }


    @Override
    public Set<Usuario> listarUsuarios(Long idAtividade) {
        Optional<Atividade> atividadeOptional = atividadeRepository.findById(idAtividade);
        if (atividadeOptional.isPresent()) {
            return atividadeOptional.get().getUsuariosResponsaveis();
        }
        throw new RuntimeException("Atividade não encontrada!");
    }
}

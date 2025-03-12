package com.Trainee.ConectaTraineeBackend.service.impl;

import com.Trainee.ConectaTraineeBackend.model.Atividade;
import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.LancamentoHorasRepository;
import com.Trainee.ConectaTraineeBackend.repository.ProjetoUsuarioRepository;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private LancamentoHorasRepository lancamentoHorasRepository;

    @Autowired
    private ProjetoUsuarioRepository projetoUsuarioRepository;


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Usuario salvarUsuario(Usuario usuario) {
        logger.info("🔐 Verificando necessidade de criptografar a senha...");

        // Evita criptografar senhas já criptografadas
        if (!usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        logger.info("✅ Senha tratada e usuário salvo: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }


    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        logger.info("Buscando usuário com ID: {}", id);
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        logger.info("Listando todos os usuários cadastrados.");
        return usuarioRepository.findAll();
    }

    @Override
    public void deletarUsuario(Long id) {
        logger.warn("⚠ Tentativa de exclusão do usuário ID: {}", id);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Verifica se o usuário possui projetos vinculados antes de tentar limpar a lista
            if (!usuario.getProjetosUsuarios().isEmpty()) {
                usuario.getProjetosUsuarios().clear();
                usuarioRepository.save(usuario); // Salva para atualizar a referência no banco
            }

            usuarioRepository.deleteById(id);
            logger.info("✅ Usuário deletado com sucesso!");
        } else {
            logger.error("❌ ERRO: Usuário com ID {} não encontrado para exclusão!", id);
        }
    }




    public void desativarUsuario(Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
            logger.info("✅ Usuário {} desativado com sucesso!", usuario.getEmail());
        } else {
            logger.warn("❌ Tentativa de desativar um usuário que não existe: ID {}", id);
        }
    }

    @Override
    public boolean temVinculacoes(Long id) {
        logger.info("🔎 Verificando se o usuário {} tem registros vinculados...", id);

        boolean temHorasLancadas = lancamentoHorasRepository.existsByUsuarioId(id);
        boolean temProjetosVinculados = projetoUsuarioRepository.existsByUsuarioId(id);

        logger.info("📌 Usuário {} possui horas lançadas? {}", id, temHorasLancadas);
        logger.info("📌 Usuário {} está vinculado a projetos? {}", id, temProjetosVinculados);

        return temHorasLancadas || temProjetosVinculados;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        logger.info("🔍 Buscando usuário pelo email: {}", email);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            logger.info("✅ Usuário encontrado: {}", usuarioOpt.get().getEmail());
        } else {
            logger.warn("⚠ Usuário não encontrado para o email: {}", email);
        }

        return usuarioOpt;
    }

    @Override
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    @Override
    public List<Usuario> listarDesativados() {
        return usuarioRepository.findByAtivoFalse();
    }


    @Override
    public Set<Atividade> listarAtividades(Long id) {
        return null;
    }
}

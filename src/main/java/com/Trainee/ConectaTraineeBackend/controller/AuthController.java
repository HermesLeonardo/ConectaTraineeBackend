package com.Trainee.ConectaTraineeBackend.controller;

import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (Postman, Frontend, etc.)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                          UsuarioRepository usuarioRepository, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;

        // Criar ADMIN automaticamente, se não existir
        criarAdminSeNecessario();
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Usuario usuario) {
        System.out.println("🔍 Tentativa de registro: " + usuario.getEmail());
        System.out.println("🛑 Senha recebida (antes da validação): [" + usuario.getSenha() + "]"); // Verifica a senha recebida

        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            System.out.println("❌ Erro: Senha está nula ou vazia!");
            return ResponseEntity.badRequest().body("Erro: Senha não pode ser nula ou vazia!");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario novoUsuario = usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "message", "Usuário cadastrado com sucesso",
                "user", novoUsuario.getEmail()
        ));
    }



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String senha = loginRequest.get("senha");

        // 🔍 Buscar o usuário no banco
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Comparar senha digitada com senha criptografada no banco
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta");
        }

        // 🏷 Gerar token JWT para o usuário autenticado
        String token = jwtUtil.generateToken(usuario.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", usuario.getEmail(),
                "role", "ROLE_" + usuario.getPerfil()  // Corrigido para retornar "ROLE_ADMIN"
        ));
    }


    private void criarAdminSeNecessario() {
        if (!usuarioRepository.existsByPerfil("ADMIN")) { // Deve ser "ADMIN", sem "ROLE_"
            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail("admin@empresa.com");
            admin.setSenha(passwordEncoder.encode("senhasegura"));
            admin.setPerfil("ADMIN"); // Salvar sem "ROLE_"

            usuarioRepository.save(admin);
            System.out.println("Usuário ADMIN criado automaticamente.");
        } else {
            System.out.println("ADMIN já existe. Nenhuma ação necessária.");
        }
    }

}

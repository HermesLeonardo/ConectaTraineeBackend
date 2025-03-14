package com.Trainee.ConectaTraineeBackend.controller.auth;

import com.Trainee.ConectaTraineeBackend.model.Usuario;
import com.Trainee.ConectaTraineeBackend.repository.UsuarioRepository;
import com.Trainee.ConectaTraineeBackend.security.JwtUtil;
import com.Trainee.ConectaTraineeBackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite requisições de qualquer origem (Postman, Frontend, etc.)


@SecuritySchemes({
        @SecurityScheme(
                name = "BearerAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
        )
})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                          UsuarioRepository usuarioRepository, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;


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



    @PostMapping(value = "/login", consumes = {"application/json", "application/x-www-form-urlencoded"})
    @Operation(summary = "Autentica o usuário e retorna um token JWT")
    public ResponseEntity<?> authenticateUser(
            @RequestBody(required = false) Map<String, String> loginRequest,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password) {

        // 🔥 Se os dados vierem como JSON, usa loginRequest
        if (loginRequest != null) {
            username = loginRequest.get("email");
            password = loginRequest.get("senha");
        }



        // 🔥 Se os dados vierem como form-urlencoded, já estão nas variáveis username e password
        if (username == null || password == null) {
            return ResponseEntity.status(400).body("Email e senha são obrigatórios.");
        }

        // 🔍 Buscar o usuário no banco
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(username);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Comparar senha digitada com senha criptografada no banco
        if (!passwordEncoder.matches(password, usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta");
        }

        // 🏷 Gerar token JWT para o usuário autenticado (inclui ID)
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getId(), usuario.getNome());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "id", usuario.getId(), // ✅ Retorna o ID do usuário
                "user", usuario.getEmail(),
                "role", "ROLE_" + usuario.getPerfil() // Corrigido para retornar "ROLE_ADMIN"
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

    private void criarUsuariosPadrãoSeNecessario() {
        // Lista de usuários padrão a serem criados
        Usuario[] usuarios = new Usuario[]{
                new Usuario("João Oliveira", "joao.oliveira@wise.com", "user123", "USER"),
                new Usuario("Maria Santos", "maria.santos@wise.com", "user123", "USER"),
                new Usuario("Roberto Lima", "roberto.lima@wise.com", "user123", "USER"),
                new Usuario("Patrícia Costa", "patricia.costa@wise.com", "user123", "USER"),
                new Usuario("Rodrigo Quisen", "rodrigo.quisen@wise.com", "admin123", "ADMIN"),
                new Usuario("Administrador", "admin@wise.com", "admin123", "ADMIN"),

        };

        // Criação dos usuários padrão, se não existirem
        for (Usuario usuario : usuarios) {
            if (!usuarioRepository.existsByEmail(usuario.getEmail())) {
                usuario.setSenha(passwordEncoder.encode(usuario.getSenha())); // Criptografa a senha
                usuarioRepository.save(usuario);
                System.out.println("Usuário " + usuario.getNome() + " criado automaticamente.");
            } else {
                System.out.println("Usuário " + usuario.getNome() + " já existe. Nenhuma ação necessária.");
            }
        }
    }

}

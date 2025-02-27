package com.Trainee.ConectaTraineeBackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("🔍 Interceptando requisição: " + path);

        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register")) {
            System.out.println("🟢 Permitir sem autenticação: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("🔴 Token ausente ou mal formatado");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        System.out.println("🔑 Token extraído para usuário: " + username);
        System.out.println("🛑 Cabeçalho Authorization: " + request.getHeader("Authorization"));


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            System.out.println("✅ Usuário encontrado no banco: " + userDetails.getUsername());
            System.out.println("👮‍♂️ Autoridades do usuário: " + userDetails.getAuthorities());

            if (!jwtUtil.validateToken(token, username)) {
                System.out.println("❌ Token inválido ou expirado para " + username);
                filterChain.doFilter(request, response);  // Interrompe a requisição caso o token seja inválido
                return;
            }

            // Se o token for válido, configura a autenticação
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("🔵 Autenticação configurada com sucesso para " + username);
            System.out.println("🔍 Autoridades do usuário autenticado: " + userDetails.getAuthorities());
        }

        System.out.println("🔍 Autenticação finalizada: " + SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }

}

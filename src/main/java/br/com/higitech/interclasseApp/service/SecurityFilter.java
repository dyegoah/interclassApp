package br.com.higitech.interclasseApp.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Obtém o token do cabeçalho da requisição (se existir)
        var token = recuperarToken(request);

        // 2. Se o utilizador enviou um token, verifica se é autêntico
        if (token != null) {
            var emailProfessor = tokenService.validarToken(token); 

            if (!emailProfessor.isEmpty()) {
                // Se a assinatura for válida, recupera o professor da base de dados
                Optional<Professor> professorOpt = professorRepository.findByEmail(emailProfessor);
                
                if(professorOpt.isPresent()) {
                    Professor professor = professorOpt.get();
                    
                    // 🛡️ PROTEÇÃO JWT (BLACKLIST): Verifica se a conta foi suspensa após a emissão do token
                    String statusConta = professor.getStatus();
                    if ("ativo".equals(statusConta) || "master".equals(statusConta)) {
                        
                        // Regista o utilizador como fiável no contexto de segurança do Spring
                        var autenticacao = new UsernamePasswordAuthenticationToken(professor, null, Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(autenticacao);
                        
                    } else {
                        // Se o administrador bloqueou a conta, o token (mesmo não expirado) é rejeitado
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"erro\": \"Sessão invalidada. A conta encontra-se suspensa ou inativa.\"}");
                        return; // Aborta a requisição imediatamente
                    }
                }
            }
        }
        
        // Prossegue com o fluxo normal
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para extrair a palavra "Bearer " do token
    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
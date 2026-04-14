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
        
        // 1. Pega o token do cabeçalho da requisição (se existir)
        var token = recuperarToken(request);

        // 2. Se a pessoa mandou um token, vamos verificar se é verdadeiro
        if (token != null) {
            var emailProfessor = tokenService.validarToken(token); // Lê o e-mail que está dentro do token

            if (!emailProfessor.isEmpty()) {
                // Se a assinatura bateu, buscamos o professor no banco
                Optional<Professor> professorOpt = professorRepository.findByEmail(emailProfessor);
                
                if(professorOpt.isPresent()) {
                    Professor professor = professorOpt.get();
                    
                    // Avisa ao Spring: "Este cara está logado e é de confiança. Pode deixar ele passar."
                    var autenticacao = new UsernamePasswordAuthenticationToken(professor, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(autenticacao);
                }
            }
        }
        
        // Continua o fluxo normal (se não tiver token, ele vai bater de cara na porta trancada do SecurityConfig)
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para tirar a palavra "Bearer " do token
    private String recuperarToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
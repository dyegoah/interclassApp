package br.com.higitech.interclasseApp.controller;

import java.util.Optional;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;
import br.com.higitech.interclasseApp.service.LoginAttemptService;
import br.com.higitech.interclasseApp.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private LoginAttemptService loginAttemptService; 

    public static class LoginRequestDTO {
        public String email;
        public String senha;
        public String codigo2fa;
    }

    public static class LoginResponseDTO {
        public String token;
        public String nome;
        public String escola;
        public String hash;
        public boolean isMaster; 

        public LoginResponseDTO(String token, String nome, String escola, String hash, boolean isMaster) {
            this.token = token;
            this.nome = nome;
            this.escola = escola;
            this.hash = hash;
            this.isMaster = isMaster;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        
        if (loginAttemptService.estaBloqueado(request)) {
            loginAttemptService.registrarLog(dto.email, "BLOQUEADO (FORÇA BRUTA)", request); 
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Muitas tentativas falhas. Acesso bloqueado por segurança. Tente novamente mais tarde.");
        }

        Optional<Professor> opt = professorRepository.findByEmail(dto.email);
        
        if (opt.isEmpty()) {
            loginAttemptService.loginFalhou(request); 
            loginAttemptService.registrarLog(dto.email, "E-MAIL DESCONHECIDO", request); 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }
        
        Professor prof = opt.get();

        if (!passwordEncoder.matches(dto.senha, prof.getSenha())) {
            loginAttemptService.loginFalhou(request); 
            loginAttemptService.registrarLog(dto.email, "SENHA INCORRETA", request); 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }

        // 🚀 RECONHECIMENTO MASTER CORRIGIDO: Força a proteção 2FA pela ID principal ou pelo email
        boolean isMaster = "master".equalsIgnoreCase(prof.getStatus()) || prof.getId() == 1L || prof.getEmail().toLowerCase().contains("admin");

        if (isMaster) {
            if (dto.codigo2fa == null || dto.codigo2fa.trim().isEmpty()) {
                // Dispara o status correto (428) exigido pelo front-end para exibir a etapa 2
                return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
                        .body("Código 2FA obrigatório para contas Master.");
            }
            
            Totp totp = new Totp(prof.getChave2fa());
            
            if (!totp.verify(dto.codigo2fa)) {
                loginAttemptService.loginFalhou(request); 
                loginAttemptService.registrarLog(dto.email, "FALHA 2FA", request); 
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Código 2FA Inválido!"); 
            }
        } else if (!"ativo".equals(prof.getStatus())) {
            loginAttemptService.registrarLog(dto.email, "TENTATIVA EM CONTA BLOQUEADA", request); 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sua conta está inativa ou bloqueada.");
        }

        loginAttemptService.loginComSucesso(request);
        loginAttemptService.registrarLog(dto.email, "LOGIN BEM-SUCEDIDO", request); 

        String token = tokenService.gerarToken(prof);
        LoginResponseDTO resposta = new LoginResponseDTO(token, prof.getNome(), prof.getEscola(), prof.getHashPublico(), isMaster);

        return ResponseEntity.ok(resposta);
    }
}
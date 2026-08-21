package br.com.higitech.interclasseApp.controller;

import java.util.Optional;

import org.jboss.aerogear.security.otp.Totp; // 🚀 Importação oficial e blindada do 2FA
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
import br.com.higitech.interclasseApp.service.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

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
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        
        Optional<Professor> opt = professorRepository.findByEmail(dto.email);
        
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }
        
        Professor prof = opt.get();

        if (!passwordEncoder.matches(dto.senha, prof.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }

        // 🚀 RECONHECIMENTO MASTER (Amarrado com os seus e-mails)
        boolean isMaster = "master".equals(prof.getStatus()) || 
                           prof.getEmail().toLowerCase().contains("admin") || 
                           "fut_sumula_pro@hotmail.com".equalsIgnoreCase(prof.getEmail()) ||
                           "dyego@master.com".equalsIgnoreCase(prof.getEmail());

        if (isMaster) {
            // Se não digitou o 2FA ainda, pede a tela pro HTML
            if (dto.codigo2fa == null || dto.codigo2fa.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
                        .body("Código 2FA obrigatório para contas Master.");
            }
            
            // 🚀 VALIDAÇÃO NATIVA DA BIBLIOTECA (Simples, direta e à prova de falhas)
            Totp totp = new Totp(prof.getChave2fa());
            if (!totp.verify(dto.codigo2fa)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Código 2FA Inválido ou Expirado! Verifique a hora do seu celular.");
            }
        } else if (!"ativo".equals(prof.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sua conta está inativa ou bloqueada.");
        }

        String token = tokenService.gerarToken(prof);
        LoginResponseDTO resposta = new LoginResponseDTO(token, prof.getNome(), prof.getEscola(), prof.getHashPublico(), isMaster);

        return ResponseEntity.ok(resposta);
    }
}
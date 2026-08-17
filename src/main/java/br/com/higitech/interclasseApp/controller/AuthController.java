package br.com.higitech.interclasseApp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jboss.aerogear.security.otp.Totp; // 🚀 Importação do Autenticador
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

    public static class LoginRequest {
        public String email;
        public String senha;
        public String codigo2fa; // 🚀 Adicionado para receber o código do Frontend
    }

    public static class RegistroRequest {
        public String nome;
        public String escola;
        public String email;
        public String senha;
    }

    @PostMapping("/login")
    public ResponseEntity<?> fazerLogin(@RequestBody LoginRequest loginRequest) {
        Optional<Professor> professorOpt = professorRepository.findByEmail(loginRequest.email);

        if (professorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail não encontrado.");
        }

        Professor professor = professorOpt.get();

        if ("bloqueado".equalsIgnoreCase(professor.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Conta bloqueada. Contate o suporte.");
        }

        // 1. Valida a Senha
        if (passwordEncoder.matches(loginRequest.senha, professor.getSenha())) {
            
            // 2. 🔒 VALIDAÇÃO DO 2FA (Se a conta tiver a chave configurada)
            if (professor.getChave2fa() != null && !professor.getChave2fa().isEmpty()) {
                if (loginRequest.codigo2fa == null || loginRequest.codigo2fa.trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA é obrigatório para contas Master!");
                }
                
                Totp totp = new Totp(professor.getChave2fa());
                if (!totp.verify(loginRequest.codigo2fa)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA Inválido ou Expirado!");
                }
            }

            // 3. Tudo certo! Gera o TOKEN REAL
            String tokenReal = tokenService.gerarToken(professor);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("id", professor.getId());
            resposta.put("nome", professor.getNome());
            resposta.put("escola", professor.getEscola());
            resposta.put("token", tokenReal); 

            return ResponseEntity.ok(resposta);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta.");
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarProfessor(@RequestBody RegistroRequest request) {
        if (professorRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este e-mail já está em uso.");
        }

        Professor novoProfessor = new Professor();
        novoProfessor.setNome(request.nome);
        novoProfessor.setEscola(request.escola);
        novoProfessor.setEmail(request.email);
        novoProfessor.setSenha(passwordEncoder.encode(request.senha));
        novoProfessor.setStatus("ativo");

        professorRepository.save(novoProfessor);
        return ResponseEntity.status(HttpStatus.CREATED).body("Conta criada com sucesso!");
    }
}
package br.com.higitech.interclasseApp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

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

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static class LoginRequest {
        public String email;
        public String senha;
        public String codigo2fa;
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

        if (passwordEncoder.matches(loginRequest.senha, professor.getSenha())) {
            
            if (professor.getChave2fa() != null && !professor.getChave2fa().isEmpty()) {
                if (loginRequest.codigo2fa == null || loginRequest.codigo2fa.trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA é obrigatório para contas Master!");
                }
                Totp totp = new Totp(professor.getChave2fa());
                if (!totp.verify(loginRequest.codigo2fa)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA Inválido ou Expirado!");
                }
            }

            String tokenReal = tokenService.gerarToken(professor);

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("id", professor.getId());
            resposta.put("hash", professor.getHashPublico()); // 🛡️ Devolve o HASH para criação do link público
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
        if (request.nome == null || request.nome.trim().length() < 3 || request.nome.length() > 50) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O nome deve ter entre 3 e 50 caracteres.");
        }
        if (request.escola == null || request.escola.trim().length() < 3 || request.escola.length() > 60) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A escola deve ter entre 3 e 60 caracteres.");
        }
        if (request.senha == null || request.senha.length() < 6 || request.senha.length() > 20) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A senha deve ter entre 6 e 20 caracteres.");
        }
        if (request.email == null || !Pattern.compile(EMAIL_PATTERN).matcher(request.email).matches()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de e-mail inválido.");
        }
        if (professorRepository.findByEmail(request.email).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este e-mail já está em uso.");
        }

        Professor novoProfessor = new Professor();
        novoProfessor.setNome(request.nome.replaceAll("<[^>]*>", "").trim());
        novoProfessor.setEscola(request.escola.replaceAll("<[^>]*>", "").trim());
        novoProfessor.setEmail(request.email.trim().toLowerCase());
        novoProfessor.setSenha(passwordEncoder.encode(request.senha));
        novoProfessor.setStatus("ativo");

        professorRepository.save(novoProfessor);
        return ResponseEntity.status(HttpStatus.CREATED).body("Conta criada com sucesso!");
    }
}
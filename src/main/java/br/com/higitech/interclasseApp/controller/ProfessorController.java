package br.com.higitech.interclasseApp.controller;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginAttemptService loginAttemptService;

    // 🔥 MAPA EM MEMÓRIA PARA RATE LIMITING (ANTISPAM E ANTI-ENUMERAÇÃO)
    private final Map<String, Long> cooldownRegistro = new ConcurrentHashMap<>();
    private final long TEMPO_COOLDOWN_REGISTRO_MS = 3 * 60 * 1000; // 3 minutos

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarProfessor(@RequestBody Professor novoProfessor, HttpServletRequest request) {
        String ipClient = getClientIp(request);

        // 🛡️ 1. BLINDAGEM CONTRA SPAM E ENUMERAÇÃO
        if (cooldownRegistro.containsKey(ipClient) && System.currentTimeMillis() < cooldownRegistro.get(ipClient)) {
            loginAttemptService.registrarLog(novoProfessor.getEmail(), "TENTATIVA DE SPAM (CADASTRO)", request);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Muitas requisições. Aguarde alguns minutos antes de tentar criar outra conta.");
        }

        try {
            // Validação de E-mail (Regex)
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            if (novoProfessor.getEmail() == null || !novoProfessor.getEmail().matches(emailRegex)) {
                cooldownRegistro.put(ipClient, System.currentTimeMillis() + TEMPO_COOLDOWN_REGISTRO_MS);
                return ResponseEntity.badRequest().body("Formato de e-mail inválido. Digite um e-mail válido.");
            }
            
            // Blacklist de E-mails Fakes
            String emailLower = novoProfessor.getEmail().toLowerCase();
            if (emailLower.contains("@teste") || emailLower.startsWith("teste") || emailLower.contains("@fake") || emailLower.contains("123456")) {
                cooldownRegistro.put(ipClient, System.currentTimeMillis() + TEMPO_COOLDOWN_REGISTRO_MS);
                return ResponseEntity.badRequest().body("Por favor, utilize o seu e-mail corporativo ou pessoal real. E-mails de teste não são permitidos.");
            }
                
            // Verifica se o e-mail já está cadastrado
            Optional<Professor> professorExistente = professorRepository.findByEmail(novoProfessor.getEmail());
            if (professorExistente.isPresent()) {
                // Aplica o cooldown de 3 minutos para impedir bots de testarem listas vazadas
                cooldownRegistro.put(ipClient, System.currentTimeMillis() + TEMPO_COOLDOWN_REGISTRO_MS);
                return ResponseEntity.badRequest().body("Este e-mail já está em uso por outra conta.");
            }

            // Criptografa a senha
            String senhaCriptografada = passwordEncoder.encode(novoProfessor.getSenha());
            novoProfessor.setSenha(senhaCriptografada);

            // Garante que o usuário nasça ativo
            if (novoProfessor.getStatus() == null || novoProfessor.getStatus().isEmpty()) {
                novoProfessor.setStatus("ativo");
            }

            // Salva no banco de dados
            professorRepository.save(novoProfessor);
            
            // Registra o sucesso e aplica o cooldown (impede a criação de contas em massa)
            loginAttemptService.registrarLog(novoProfessor.getEmail(), "NOVA CONTA CRIADA", request);
            cooldownRegistro.put(ipClient, System.currentTimeMillis() + TEMPO_COOLDOWN_REGISTRO_MS);

            return ResponseEntity.ok().body("Conta criada com sucesso!");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno no servidor.");
        }
    }
}
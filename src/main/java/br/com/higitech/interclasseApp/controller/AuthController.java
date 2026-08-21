package br.com.higitech.interclasseApp.controller;

import java.nio.ByteBuffer;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.jboss.aerogear.security.otp.api.Base32;
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
        public boolean isMaster; // 🚀 ETIQUETA MASTER PARA O FRONTEND

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

        // 🚀 RECONHECIMENTO BLINDADO: Mesmo se o banco errar o status, o e-mail garante o acesso Master!
        boolean isMaster = "master".equals(prof.getStatus()) || 
                           prof.getEmail().toLowerCase().contains("admin") || 
                           "fut_sumula_pro@hotmail.com".equalsIgnoreCase(prof.getEmail());

        if (isMaster) {
            if (dto.codigo2fa == null || dto.codigo2fa.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.PRECONDITION_REQUIRED)
                        .body("Código 2FA obrigatório para contas Master.");
            }
            
            if (!validarTotpComTolerancia(prof.getChave2fa(), dto.codigo2fa)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Código 2FA Inválido ou Expirado! Verifique a hora do seu celular.");
            }
        } else if (!"ativo".equals(prof.getStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sua conta está inativa ou bloqueada.");
        }

        String token = tokenService.gerarToken(prof);
        
        // Envia a resposta com a Etiqueta Master
        LoginResponseDTO resposta = new LoginResponseDTO(token, prof.getNome(), prof.getEscola(), prof.getHashPublico(), isMaster);

        return ResponseEntity.ok(resposta);
    }

    private boolean validarTotpComTolerancia(String chaveSecreta, String codigoDigitado) {
        if (chaveSecreta == null || chaveSecreta.isEmpty()) return false;
        long timeWindow = System.currentTimeMillis() / 30000L;
        for (int i = -1; i <= 1; i++) {
            if (gerarTotp(chaveSecreta, timeWindow + i).equals(codigoDigitado)) {
                return true;
            }
        }
        return false;
    }

    private String gerarTotp(String chaveSecreta, long timeWindow) {
        try {
            byte[] bytesChave = Base32.decode(chaveSecreta);
            
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(bytesChave, "HmacSHA1"));
            
            byte[] data = ByteBuffer.allocate(8).putLong(timeWindow).array();
            byte[] hash = mac.doFinal(data);
            
            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }
            
            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 1000000;
            
            return String.format("%06d", truncatedHash);
        } catch (Exception e) {
            return "";
        }
    }
}
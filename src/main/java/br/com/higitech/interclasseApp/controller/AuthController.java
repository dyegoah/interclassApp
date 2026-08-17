package br.com.higitech.interclasseApp.controller;

import java.util.Map;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.service.TokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> dadosLogin) {
        try {
            String email = dadosLogin.get("email");
            String senha = dadosLogin.get("senha");
            String codigo2fa = dadosLogin.get("codigo2fa");

            // 1. Valida Email e Senha primeiro
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, senha);
            Authentication auth = authenticationManager.authenticate(authToken);
            Professor profLogado = (Professor) auth.getPrincipal();

            // 2. 🔒 VALIDAÇÃO DO 2FA (GOOGLE AUTHENTICATOR)
            if (profLogado.getChave2fa() != null && !profLogado.getChave2fa().isEmpty()) {
                if (codigo2fa == null || codigo2fa.trim().isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"erro\": \"Código 2FA é obrigatório!\"}");
                }
                
                Totp totp = new Totp(profLogado.getChave2fa());
                if (!totp.verify(codigo2fa)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"erro\": \"Código 2FA Inválido ou Expirado!\"}");
                }
            }

            // 3. Tudo certo! Gera o JWT
            String tokenJWT = tokenService.gerarToken(profLogado);
            return ResponseEntity.ok(Map.of("token", tokenJWT));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"erro\": \"Email ou senha incorretos!\"}");
        }
    }
}
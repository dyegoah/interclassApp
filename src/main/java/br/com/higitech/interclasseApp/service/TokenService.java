package br.com.higitech.interclasseApp.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.higitech.interclasseApp.model.Professor;

@Service
public class TokenService {

    // ⚠️ ATENÇÃO: No futuro (no Render), essa senha deve vir das Variáveis de Ambiente!
    // Por enquanto, usaremos essa senha forte fixa para testes.
    private final String segredo = "H1ghT3ch-4r3n4-S3cur1ty-K3y-2026!";

    // 1. MÉTODO QUE GERA O CRACHÁ
    public String gerarToken(Professor professor) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(segredo);
            return JWT.create()
                    .withIssuer("interclass-arena") // Quem emitiu
                    .withSubject(professor.getEmail()) // O dono do crachá
                    .withClaim("id", professor.getId()) // Guarda o ID do professor dentro do token
                    .withExpiresAt(gerarDataExpiracao()) // Validade de 2 horas
                    .sign(algoritmo); // Assina criptograficamente
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar o token JWT", exception);
        }
    }

    // 2. MÉTODO QUE LÊ E VALIDA O CRACHÁ NA PORTA
    public String validarToken(String token) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(segredo);
            return JWT.require(algoritmo)
                    .withIssuer("interclass-arena")
                    .build()
                    .verify(token)
                    .getSubject(); // Se der certo, devolve o e-mail do professor
        } catch (JWTVerificationException exception) {
            return ""; // Se o token for falso, alterado ou vencido, devolve vazio (bloqueia)
        }
    }

    // O token vale por 2 horas. Depois disso, o professor tem que logar de novo.
    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
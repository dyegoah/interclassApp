package br.com.higitech.interclasseApp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@RestController
@RequestMapping("/api/professores") // <-- Esta é a base da URL
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔥 AQUI ESTÁ A ROTA QUE O FRONTEND ESTÁ PROCURANDO
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarProfessor(@RequestBody Professor novoProfessor) {
        
        // 1. Verifica se o e-mail já está cadastrado no banco
        Optional<Professor> professorExistente = professorRepository.findByEmail(novoProfessor.getEmail());
        if (professorExistente.isPresent()) {
            // Retorna erro 400 (Bad Request) se já existir
            return ResponseEntity.badRequest().body("Este e-mail já está em uso por outra conta.");
        }

        // 2. Criptografa a senha antes de salvar (MUITO IMPORTANTE)
        String senhaCriptografada = passwordEncoder.encode(novoProfessor.getSenha());
        novoProfessor.setSenha(senhaCriptografada);

        // 3. Garante que o usuário nasça ativo
        if (novoProfessor.getStatus() == null || novoProfessor.getStatus().isEmpty()) {
            novoProfessor.setStatus("ativo");
        }

        // 4. Salva no banco de dados
        professorRepository.save(novoProfessor);

        // Retorna sucesso (200 OK)
        return ResponseEntity.ok().body("Conta criada com sucesso!");
    }
}
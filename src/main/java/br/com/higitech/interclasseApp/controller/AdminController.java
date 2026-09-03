package br.com.higitech.interclasseApp.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // 🔥 IMPORT CORRETO (Spring Data, não Hibernate)
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🛡️ O CADEADO MASTER: Sincronizado com o AuthController
    private boolean isSuperAdmin(Professor professor) {
        if (professor == null || professor.getEmail() == null) return false;
        
        String email = professor.getEmail().toLowerCase();
        
        return "master".equals(professor.getStatus()) || 
               email.contains("admin") || 
               "fut_sumula_pro@hotmail.com".equals(email) ||
               "dyego@master.com".equals(email);
    }

    // 🔥 CORREÇÃO: Listagem segura, bloqueada apenas para Super Admin e com Paginação para não travar a RAM
    @GetMapping("/professores")
    public ResponseEntity<?> listarProfessores(
            @PageableDefault(size = 50, sort = "id") Pageable pageable, 
            @AuthenticationPrincipal Professor adminLogado) {
        
        if (!isSuperAdmin(adminLogado)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        
        Page<Professor> paginaProfessores = professorRepository.findAll(pageable);
        return ResponseEntity.ok(paginaProfessores);
    }

    @DeleteMapping("/professores/{id}")
    public ResponseEntity<?> excluirProfessor(@PathVariable Long id, @AuthenticationPrincipal Professor adminLogado) {
        if (!isSuperAdmin(adminLogado)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        
        professorRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/professores/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> payload, @AuthenticationPrincipal Professor adminLogado) {
        if (!isSuperAdmin(adminLogado)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        
        Optional<Professor> prof = professorRepository.findById(id);
        if (prof.isPresent()) {
            Professor p = prof.get();
            p.setStatus(payload.get("status"));
            professorRepository.save(p);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/professores/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable Long id, @RequestBody Map<String, String> payload, @AuthenticationPrincipal Professor adminLogado) {
        if (!isSuperAdmin(adminLogado)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acesso negado.");
        
        Optional<Professor> prof = professorRepository.findById(id);
        if (prof.isPresent()) {
            Professor p = prof.get();
            p.setSenha(passwordEncoder.encode(payload.get("senha")));
            professorRepository.save(p);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
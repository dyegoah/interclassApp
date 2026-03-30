package br.com.higitech.interclasseApp.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    // O header 'X-Tenant-ID' simula a escola logada para o nosso futuro multi-tenant
    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestHeader(value = "X-Tenant-ID", defaultValue = "1") String tenantId) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Simulando dados do banco para a escola atual
        response.put("escola", "Colégio Estadual " + tenantId);
        response.put("totalAlunosInscritos", 145);
        response.put("modalidadesAtivas", 4);
        
        // Lista de modalidades com suas cores (para o frontend estilizar)
        List<Map<String, Object>> modalidades = Arrays.asList(
            Map.of("id", 1, "nome", "Futsal", "alunos", 60, "corTema", "rgba(46, 204, 113, 0.2)"),   // Verde
            Map.of("id", 2, "nome", "Vôlei", "alunos", 45, "corTema", "rgba(52, 152, 219, 0.2)"),    // Azul
            Map.of("id", 3, "nome", "Basquete", "alunos", 30, "corTema", "rgba(230, 126, 34, 0.2)"), // Laranja
            Map.of("id", 4, "nome", "Queimada", "alunos", 10, "corTema", "rgba(155, 89, 182, 0.2)")  // Roxo
        );
        
        response.put("modalidades", modalidades);

        return ResponseEntity.ok(response);
    }
}
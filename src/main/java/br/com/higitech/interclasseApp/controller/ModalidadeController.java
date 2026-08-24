package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;

@RestController
@RequestMapping("/api/modalidades")
public class ModalidadeController {

    @Autowired
    private ModalidadeRepository modalidadeRepository;

    @GetMapping("/public/{hashPublico}")
    public ResponseEntity<List<Modalidade>> getModalidadesPublicas(@PathVariable String hashPublico) {
        List<Modalidade> todas = modalidadeRepository.findByLoteProfessorHashPublico(hashPublico);
        
        // 🔥 FILTRO BLINDADO: Pega APENAS as modalidades ativas no lote "geral" (Inscrições)
        List<Modalidade> ativas = todas.stream()
                .filter(m -> m.getLote() != null && "geral".equalsIgnoreCase(m.getLote().getGenero()))
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(ativas);
    }
}
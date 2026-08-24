package br.com.higitech.interclasseApp.controller;

import java.util.List;

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
        // 🔥 CORREÇÃO: Pega SOMENTE os esportes liberados na tela de inscrição (Lote "geral")
        List<Modalidade> modalidades = modalidadeRepository.findByLoteProfessorHashPublicoAndLoteGenero(hashPublico, "geral");
        return ResponseEntity.ok(modalidades);
    }
}
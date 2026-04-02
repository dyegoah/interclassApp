package br.com.higitech.interclasseApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository; // Crie esta interface se não existir

@RestController
@RequestMapping("/api/modalidades")
@CrossOrigin(origins = "*")
public class ModalidadeController {

    private final ModalidadeRepository modalidadeRepository;

    public ModalidadeController(ModalidadeRepository modalidadeRepository) {
        this.modalidadeRepository = modalidadeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Modalidade>> listarModalidades() {
        return ResponseEntity.ok(modalidadeRepository.findAll());
    }
}
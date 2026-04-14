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

    // 🔓 ROTA PÚBLICA: Devolve os esportes ativos apenas da escola do link
    @GetMapping("/public/{professorId}")
    public ResponseEntity<List<Modalidade>> listarModalidadesDaEscola(@PathVariable Long professorId) {
        List<Modalidade> modalidades = modalidadeRepository.findByLoteProfessorId(professorId);
        return ResponseEntity.ok(modalidades);
    }
}
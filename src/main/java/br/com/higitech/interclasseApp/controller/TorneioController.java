package br.com.higitech.interclasseApp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.TorneioDTO;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.service.TorneioService;

@RestController
@RequestMapping("/api/torneios")
@CrossOrigin(origins = "*")
public class TorneioController {

    private final TorneioService torneioService;

    public TorneioController(TorneioService torneioService) {
        this.torneioService = torneioService;
    }

    @PostMapping
    public ResponseEntity<Torneio> criarTorneio(@RequestBody TorneioDTO dto) {
        Torneio salvo = torneioService.criarTorneio(dto);
        System.out.println("Novo Torneio Inserido: " + salvo.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Torneio>> listarTorneios() {
        return ResponseEntity.ok(torneioService.listarTodos());
    }
}
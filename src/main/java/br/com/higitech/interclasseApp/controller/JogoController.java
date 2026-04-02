package br.com.higitech.interclasseApp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.CalendarioSaveDTO;
import br.com.higitech.interclasseApp.dto.JogoDTO;
import br.com.higitech.interclasseApp.service.JogoService;

@RestController
@RequestMapping("/api/jogos")
@CrossOrigin(origins = "*")
public class JogoController {

    private final JogoService jogoService;

    // CONSTRUTOR MANUAL
    public JogoController(JogoService jogoService) {
        this.jogoService = jogoService;
    }

    @PostMapping("/calendario")
    public ResponseEntity<Void> salvarCalendarioOficial(@RequestBody CalendarioSaveDTO calendarioDTO) {
        jogoService.salvarLoteDeJogos(calendarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/lote/{genero}")
    public ResponseEntity<List<JogoDTO>> listarJogosDoPlayHub(@PathVariable String genero) {
        List<JogoDTO> jogos = jogoService.buscarJogosParaPlayHub(genero);
        return ResponseEntity.ok(jogos);
    }
}
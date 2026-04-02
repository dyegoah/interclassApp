package br.com.higitech.interclasseApp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.dto.LoteSetupDTO;
import br.com.higitech.interclasseApp.service.SetupService;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*") 
public class SetupController {

    private final SetupService setupService;

    // CONSTRUTOR MANUAL
    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    @PostMapping
    public ResponseEntity<Void> salvarConfiguracoesIniciais(@RequestBody LoteSetupDTO setupDTO) {
        setupService.processarLote(setupDTO);
        System.out.println("HigTec API: Setup do Lote " + setupDTO.genero() + " recebido com sucesso!");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
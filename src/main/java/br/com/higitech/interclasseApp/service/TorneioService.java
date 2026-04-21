package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.controller.TorneioController.EsporteSetup;
import br.com.higitech.interclasseApp.controller.TorneioController.SetupDTO;
import br.com.higitech.interclasseApp.dto.TorneioDTO;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.repositories.TorneioRepository;

@Service
public class TorneioService {

    @Autowired
    private TorneioRepository torneioRepository;

    @Transactional
    public void salvarSetupCompleto(SetupDTO payload, Professor professorLogado) {
        for (EsporteSetup esporte : payload.esportes) {
            Torneio torneio = new Torneio();
            torneio.setTitulo(payload.genero.toUpperCase() + " | Esporte ID: " + esporte.id + " | Formato: " + esporte.formato);
            torneio.setStatus("CONFIGURADO");
            torneioRepository.save(torneio);
        }
    }

    public Torneio criarTorneio(TorneioDTO dto, Professor professorLogado) {
        Torneio torneio = new Torneio();
        torneio.setTitulo(dto.getTitulo()); 
        torneio.setStatus("ATIVO");
        return torneioRepository.save(torneio);
    }

    public List<Torneio> listarTodos(Professor professorLogado) {
        // Como o BD atual não tem professor_id em Torneio, listamos todos:
        return torneioRepository.findAll();
    }
}
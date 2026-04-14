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

    // ⚙️ O MOTOR DO SETUP: Salva as regras de todos os esportes escolhidos
    @Transactional
    public void salvarSetupCompleto(SetupDTO payload, Professor professorLogado) {
        
        // Para cada esporte configurado na tela, cria um Torneio no banco
        for (EsporteSetup esporte : payload.esportes) {
            Torneio torneio = new Torneio();
            
            // Cria um título descritivo. Ex: "Masculino - ID 1 (Futsal) - Mata-Mata"
            torneio.setTitulo(payload.genero.toUpperCase() + " | Esporte ID: " + esporte.id + " | Formato: " + esporte.formato);
            torneio.setStatus("CONFIGURADO");
            torneio.setProfessor(professorLogado); 
            
            torneioRepository.save(torneio);
        }
    }

    public Torneio criarTorneio(TorneioDTO dto, Professor professorLogado) {
        Torneio torneio = new Torneio();
        // Substitua getTitulo() pelo método correto caso o seu TorneioDTO seja um Record
        torneio.setTitulo(dto.getTitulo()); 
        torneio.setStatus("ATIVO");
        torneio.setProfessor(professorLogado); 
        
        return torneioRepository.save(torneio);
    }

    public List<Torneio> listarTodos(Professor professorLogado) {
        return torneioRepository.findByProfessorId(professorLogado.getId());
    }
}
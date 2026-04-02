package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.JogadorStatsDTO;
import br.com.higitech.interclasseApp.dto.SumulaFinalizadaDTO;
import br.com.higitech.interclasseApp.model.Aluno;
import br.com.higitech.interclasseApp.model.EventoSumula;
import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;
import br.com.higitech.interclasseApp.repositories.EventoSumulaRepository;
import br.com.higitech.interclasseApp.repositories.JogoRepository;

@Service
public class SumulaService {

    private final JogoRepository jogoRepository;
    private final AlunoRepository alunoRepository;
    private final EventoSumulaRepository eventoSumulaRepository;

    // CONSTRUTOR MANUAL (Substitui o Lombok)
    public SumulaService(JogoRepository jogoRepository, AlunoRepository alunoRepository, EventoSumulaRepository eventoSumulaRepository) {
        this.jogoRepository = jogoRepository;
        this.alunoRepository = alunoRepository;
        this.eventoSumulaRepository = eventoSumulaRepository;
    }

    @Transactional
    public void processarSumula(Long jogoId, SumulaFinalizadaDTO dto) {
        
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado na base de dados!"));
        
        jogo.setPlacarA(dto.placar().A());
        jogo.setPlacarB(dto.placar().B());
        jogo.setStatus("FINALIZADO");
        jogoRepository.save(jogo);

        if (dto.escalacao().containsKey("A")) {
            salvarEstatisticasEquipe(jogo, dto.escalacao().get("A"));
        }
        if (dto.escalacao().containsKey("B")) {
            salvarEstatisticasEquipe(jogo, dto.escalacao().get("B"));
        }
    }

    private void salvarEstatisticasEquipe(Jogo jogo, List<JogadorStatsDTO> atletas) {
        for (JogadorStatsDTO atletaDTO : atletas) {
            if (atletaDTO.stats() == null || atletaDTO.stats().isEmpty()) continue;

            Aluno aluno = alunoRepository.findById(Long.valueOf(atletaDTO.id().replace("A", "").replace("B", "")))
                    .orElse(null);
            
            if (aluno == null) continue;

            for (String icone : atletaDTO.stats()) {
                EventoSumula evento = new EventoSumula();
                evento.setJogo(jogo);
                evento.setAluno(aluno);
                evento.setIconeAcao(icone);
                evento.setPontosGerados(calcularPontosPorIcone(icone));
                eventoSumulaRepository.save(evento);
            }
        }
    }

    private Integer calcularPontosPorIcone(String icone) {
        return switch (icone) {
            case "⚽", "🏐", "🤾", "🎯", "🏓", "✅", "👑", "🎮", "🧱", "☄️" -> 1;
            case "🏀" -> 2;
            case "🔥" -> 3;
            case "🥇" -> 3; 
            case "🥈" -> 2; 
            case "🥉" -> 1; 
            default -> 0; 
        };
    }
}
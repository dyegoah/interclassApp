package br.com.higitech.interclasseApp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.CalendarioSaveDTO;
import br.com.higitech.interclasseApp.dto.JogoDTO;
import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.repositories.JogoRepository;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;

    // CONSTRUTOR MANUAL (Substitui o Lombok)
    public JogoService(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }

    @Transactional
    public void salvarLoteDeJogos(CalendarioSaveDTO dto) {
        List<Jogo> jogosParaSalvar = dto.jogos().stream().map(jogoDTO -> {
            Jogo jogo = new Jogo();
            jogo.setTitulo(jogoDTO.titulo());
            jogo.setQuadra(jogoDTO.quadra());
            jogo.setStatus("PENDENTE"); 
            
            jogo.setDataJogo(LocalDate.parse(jogoDTO.diaId())); 
            jogo.setHorario(LocalTime.parse(jogoDTO.hora(), DateTimeFormatter.ofPattern("HH:mm")));
            
            return jogo;
        }).toList();

        jogoRepository.saveAll(jogosParaSalvar);
    }

    @Transactional(readOnly = true)
    public List<JogoDTO> buscarJogosParaPlayHub(String genero) {
        List<Jogo> jogos = jogoRepository.findByModalidadeLoteGeneroOrderByDataJogoAscHorarioAsc(genero);
        
        return jogos.stream().map(j -> new JogoDTO(
                j.getId(),
                j.getDataJogo().toString(),
                j.getHorario().toString(),
                j.getModalidade().getNomeEsporte(),
                j.getModalidade().getIcone(),
                j.getTitulo(),
                j.getQuadra()
        )).toList();
    }
}
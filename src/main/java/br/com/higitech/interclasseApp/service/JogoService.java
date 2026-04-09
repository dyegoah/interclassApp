package br.com.higitech.interclasseApp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.CalendarioSaveDTO;
import br.com.higitech.interclasseApp.dto.JogoDTO;
import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Lote;
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.repositories.JogoRepository;
import br.com.higitech.interclasseApp.repositories.LoteRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final LoteRepository loteRepository;
    private final ProfessorRepository professorRepository; // 🌟 Novo Repositório

    public JogoService(JogoRepository jogoRepository, ModalidadeRepository modalidadeRepository, 
                       LoteRepository loteRepository, ProfessorRepository professorRepository) {
        this.jogoRepository = jogoRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.loteRepository = loteRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional
    public void salvarLoteDeJogos(CalendarioSaveDTO dto) {
        
        // 🌟 MOCK: Professor Fantasma (ID 1)
        Professor profLogado = professorRepository.findById(1L).orElseThrow(() -> new RuntimeException("Professor base não existe. Vá na aba de Setup primeiro!"));
        
        // 1. Encontra ou cria o Lote (Gênero) Deste Professor
        List<Lote> lotesSalvos = loteRepository.findByProfessorId(1L);
        Lote loteCorreto = lotesSalvos.stream()
                .filter(l -> l.getGenero() != null && l.getGenero().equalsIgnoreCase(dto.genero()))
                .findFirst()
                .orElse(null);

        if (loteCorreto == null) {
            loteCorreto = new Lote();
            loteCorreto.setGenero(dto.genero());
            loteCorreto.setProfessor(profLogado); // 🔒 Atrela ao professor
            loteCorreto = loteRepository.save(loteCorreto);
        }

        final Lote loteParaSalvar = loteCorreto;
        List<Modalidade> modalidadesSalvas = new java.util.ArrayList<>(modalidadeRepository.findAll());

        List<Jogo> jogosParaSalvar = dto.jogos().stream().map(jogoDTO -> {
            Jogo jogo = new Jogo();
            jogo.setTitulo(jogoDTO.titulo());
            jogo.setQuadra(jogoDTO.quadra());
            jogo.setStatus("PENDENTE"); 
            jogo.setGenero(dto.genero()); 
            jogo.setProfessor(profLogado); // 🔒 Jogo atrelado ao professor!
            
            jogo.setDataJogo(LocalDate.parse(jogoDTO.diaId())); 
            jogo.setHorario(LocalTime.parse(jogoDTO.hora(), DateTimeFormatter.ofPattern("HH:mm")));
            
            jogo.setEquipeAId(jogoDTO.equipeAId());
            jogo.setEquipeANome(jogoDTO.equipeANome());
            jogo.setEquipeBId(jogoDTO.equipeBId());
            jogo.setEquipeBNome(jogoDTO.equipeBNome());

            Modalidade modCorreta = modalidadesSalvas.stream()
                    .filter(m -> m.getNomeEsporte().equalsIgnoreCase(jogoDTO.esporte()))
                    .findFirst()
                    .orElse(null);
            
            if (modCorreta == null) {
                modCorreta = new Modalidade();
                modCorreta.setNomeEsporte(jogoDTO.esporte());
                modCorreta.setIcone(jogoDTO.icone());
                modCorreta.setLote(loteParaSalvar);
                modCorreta = modalidadeRepository.save(modCorreta);
                modalidadesSalvas.add(modCorreta);
            }
            
            jogo.setModalidade(modCorreta); 
            return jogo;
        }).toList();

        jogoRepository.saveAll(jogosParaSalvar);
    }

    @Transactional(readOnly = true)
    public List<JogoDTO> buscarJogosParaPlayHub(String genero) {
        // 🔒 Filtra apenas os jogos do Professor Fantasma (ID 1)
        List<Jogo> jogos = jogoRepository.findByProfessorIdAndGeneroOrderByDataJogoAscHorarioAsc(1L, genero);
        
        return jogos.stream().map(j -> new JogoDTO(
                j.getId(),
                j.getDataJogo() != null ? j.getDataJogo().toString() : "Data Indefinida",
                j.getHorario() != null ? j.getHorario().toString() : "Hora Indefinida",
                j.getModalidade() != null ? j.getModalidade().getNomeEsporte() : "Esporte",
                j.getModalidade() != null ? j.getModalidade().getIcone() : "🏆",
                j.getTitulo(), j.getQuadra(),
                j.getEquipeAId(), j.getEquipeANome(),
                j.getEquipeBId(), j.getEquipeBNome()
        )).toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buscarJogoDetalhadoParaSumula(Long id) {
        Jogo jogo = jogoRepository.findById(id).orElseThrow(() -> new RuntimeException("Jogo não encontrado no banco"));
            
        Map<String, Object> map = new HashMap<>();
        map.put("id", jogo.getId());
        map.put("titulo", jogo.getTitulo());
        map.put("status", jogo.getStatus());
        map.put("placarA", jogo.getPlacarA());
        map.put("placarB", jogo.getPlacarB());
        map.put("equipeAId", jogo.getEquipeAId());
        map.put("equipeANome", jogo.getEquipeANome());
        map.put("equipeBId", jogo.getEquipeBId());
        map.put("equipeBNome", jogo.getEquipeBNome());
        
        Map<String, String> modMap = new HashMap<>();
        if (jogo.getModalidade() != null) {
            modMap.put("nomeEsporte", jogo.getModalidade().getNomeEsporte());
            modMap.put("icone", jogo.getModalidade().getIcone());
        }
        map.put("modalidade", modMap);
        map.put("escalacoes", List.of()); 
        return map;
    }
}
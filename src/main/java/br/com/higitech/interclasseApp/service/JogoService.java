package br.com.higitech.interclasseApp.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
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
import jakarta.annotation.PostConstruct;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final LoteRepository loteRepository;
    private final JdbcTemplate jdbcTemplate;

    public JogoService(JogoRepository jogoRepository, ModalidadeRepository modalidadeRepository, LoteRepository loteRepository, JdbcTemplate jdbcTemplate) {
        this.jogoRepository = jogoRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.loteRepository = loteRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void higienizarBancoDeDados() {
        System.out.println("🔄 Iniciando higienização estrutural do Banco de Dados...");
        try { jdbcTemplate.execute("ALTER TABLE jogos ALTER COLUMN dia_id DROP NOT NULL"); } catch (Exception e) {}
        try { jdbcTemplate.execute("ALTER TABLE jogos ALTER COLUMN esporte DROP NOT NULL"); } catch (Exception e) {}
        try { jdbcTemplate.execute("ALTER TABLE jogos ALTER COLUMN icone DROP NOT NULL"); } catch (Exception e) {}
        try { jdbcTemplate.execute("ALTER TABLE jogos ALTER COLUMN hora DROP NOT NULL"); } catch (Exception e) {}
        try { jdbcTemplate.execute("ALTER TABLE tb_lote DROP CONSTRAINT IF EXISTS tb_lote_genero_key"); } catch (Exception e) {}
        try { jdbcTemplate.execute("ALTER TABLE tb_lote DROP CONSTRAINT IF EXISTS uk_tb_lote_genero"); } catch (Exception e) {}
        System.out.println("✅ Travas fantasmas removidas. Servidor Multi-Tenant pronto.");
    }

    @Transactional
    public void salvarLoteDeJogos(CalendarioSaveDTO dto, Professor profLogado) {
        // Limpa os jogos antigos daquele professor específico
        jogoRepository.deleteByProfessorIdAndGenero(profLogado.getId(), dto.genero());

        // 🛡️ CORREÇÃO MULTI-TENANT: Busca o lote exclusivo deste professor! (Sem findAll)
        Lote loteCorreto = loteRepository.findByProfessorIdAndGenero(profLogado.getId(), dto.genero()).orElse(null);

        if (loteCorreto == null) {
            loteCorreto = new Lote();
            loteCorreto.setGenero(dto.genero());
            loteCorreto.setProfessor(profLogado);
            loteCorreto = loteRepository.save(loteCorreto);
        }

        final Lote loteParaSalvar = loteCorreto;
        
        // 🛡️ CORREÇÃO MULTI-TENANT: Busca modalidades apenas deste Lote (Sem findAll)
        List<Modalidade> modalidadesSalvas = new java.util.ArrayList<>(modalidadeRepository.findByLoteId(loteParaSalvar.getId()));

        List<Jogo> jogosParaSalvar = dto.jogos().stream().map(jogoDTO -> {
            Jogo jogo = new Jogo();
            jogo.setTitulo(jogoDTO.titulo());
            jogo.setQuadra(jogoDTO.quadra());
            jogo.setStatus("PENDENTE"); 
            jogo.setGenero(dto.genero()); 
            jogo.setProfessor(profLogado); 
            
            try {
                if (jogoDTO.diaId() != null && !jogoDTO.diaId().isEmpty()) {
                    jogo.setDataJogo(LocalDate.parse(jogoDTO.diaId())); 
                }
                if (jogoDTO.hora() != null && !jogoDTO.hora().isEmpty()) {
                    jogo.setHorario(LocalTime.parse(jogoDTO.hora(), DateTimeFormatter.ofPattern("HH:mm")));
                }
            } catch (Exception e) {
                System.out.println("Aviso: Formato de data/hora ignorado.");
            }
            
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
    public List<JogoDTO> buscarJogosParaPlayHub(String genero, Professor profLogado) {
        List<Jogo> jogos = jogoRepository.findByProfessorIdAndGenero(profLogado.getId(), genero);
        
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

    @Transactional
    public void excluirTorneioEspecifico(String genero, String esporte, Professor profLogado) {
        jogoRepository.excluirTorneioEspecificoDoBanco(profLogado.getId(), genero, esporte);
    }
}
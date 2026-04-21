package br.com.higitech.interclasseApp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.EsporteSetupDTO;
import br.com.higitech.interclasseApp.dto.LoteSetupDTO;
import br.com.higitech.interclasseApp.model.Classificacao;
import br.com.higitech.interclasseApp.model.Lote;
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.repositories.ClassificacaoRepository;
import br.com.higitech.interclasseApp.repositories.LoteRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;
import br.com.higitech.interclasseApp.repositories.ProfessorRepository;
import br.com.higitech.interclasseApp.repositories.TorneioRepository;

@Service
@Transactional
public class SetupService {

    private final TorneioRepository torneioRepository;
    private final LoteRepository loteRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final ClassificacaoRepository classificacaoRepository;
    private final ProfessorRepository professorRepository;

    public SetupService(TorneioRepository torneioRepository, LoteRepository loteRepository, 
                        ModalidadeRepository modalidadeRepository, ClassificacaoRepository classificacaoRepository,
                        ProfessorRepository professorRepository) {
        this.torneioRepository = torneioRepository;
        this.loteRepository = loteRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.classificacaoRepository = classificacaoRepository;
        this.professorRepository = professorRepository;
    }

    public void processarLote(LoteSetupDTO dto) {
        Professor profLogado = professorRepository.findById(1L).orElseGet(() -> {
            Professor p = new Professor();
            p.setNome("Admin Sistema");
            p.setEscola("Escola Padrão");
            p.setEmail("admin@futsumula.com");
            p.setSenha("123456");
            return professorRepository.save(p);
        });

        // Pega o primeiro torneio disponível ou cria um básico seguro para o BD
        Torneio torneioAtivo = torneioRepository.findAll().stream()
                .findFirst() 
                .orElseGet(() -> {
                    Torneio t = new Torneio();
                    t.setTitulo("Interclasses Oficial");
                    t.setStatus("ATIVO");
                    return torneioRepository.save(t);
                });

        Lote lote = new Lote();
        lote.setGenero(dto.genero());
        lote.setTorneio(torneioAtivo);
        lote.setProfessor(profLogado);
        lote = loteRepository.save(lote);

        for (EsporteSetupDTO esporteDto : dto.esportes()) {
            Modalidade modalidade = new Modalidade();
            modalidade.setNomeEsporte(obterNomeEsporte(esporteDto.id()));
            modalidade.setLote(lote);
            
            Modalidade modalidadeSalva = modalidadeRepository.save(modalidade);

            if ("liga".equals(esporteDto.formato()) || "grupos".equals(esporteDto.formato()) || "liga_playoffs".equals(esporteDto.formato())) {
                for (int i = 1; i <= esporteDto.qtdTimes(); i++) {
                    Classificacao classif = new Classificacao();
                    classif.setModalidade(modalidadeSalva);
                    classif.setNomeTurma("Equipe " + i + " (A Definir)");
                    classificacaoRepository.save(classif);
                }
            }
        }
    }

    private String obterNomeEsporte(Integer id) {
        switch (id) {
            case 1: return "Futsal"; case 2: return "Vôlei"; case 3: return "Basquete";
            case 4: return "Handebol"; case 5: return "Queimada"; case 6: return "Vôlei de Areia";
            case 7: return "Natação"; case 8: return "Atletismo"; case 9: return "Tênis de Mesa"; 
            case 10: return "Xadrez"; case 11: return "Dama"; case 12: return "E-Sports"; 
            default: return "Esporte Geral";
        }
    }
}
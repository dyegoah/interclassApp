package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.EsporteSetupDTO;
import br.com.higitech.interclasseApp.dto.LoteSetupDTO;
import br.com.higitech.interclasseApp.model.Aluno;
import br.com.higitech.interclasseApp.model.Classificacao;
import br.com.higitech.interclasseApp.model.Jogo;
import br.com.higitech.interclasseApp.model.Lote;
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.model.Professor;
import br.com.higitech.interclasseApp.model.Torneio;
import br.com.higitech.interclasseApp.repositories.AlunoRepository;
import br.com.higitech.interclasseApp.repositories.ClassificacaoRepository;
import br.com.higitech.interclasseApp.repositories.JogoRepository;
import br.com.higitech.interclasseApp.repositories.LoteRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;
import br.com.higitech.interclasseApp.repositories.TorneioRepository;

@Service
@Transactional
public class SetupService {

    private final TorneioRepository torneioRepository;
    private final LoteRepository loteRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final ClassificacaoRepository classificacaoRepository;
    private final JogoRepository jogoRepository;
    private final AlunoRepository alunoRepository;

    public SetupService(TorneioRepository torneioRepository, LoteRepository loteRepository, 
                        ModalidadeRepository modalidadeRepository, ClassificacaoRepository classificacaoRepository,
                        JogoRepository jogoRepository, AlunoRepository alunoRepository) {
        this.torneioRepository = torneioRepository;
        this.loteRepository = loteRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.classificacaoRepository = classificacaoRepository;
        this.jogoRepository = jogoRepository;
        this.alunoRepository = alunoRepository;
    }

    public void processarLote(LoteSetupDTO dto) {
        Professor profLogado = (Professor) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

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

    // 🔴 O CÓDIGO DA FAXINA COMPLETA
    public void resetarTudo(Professor profLogado) {
        Long idProf = profLogado.getId();
        
        // 1. Apaga todos os jogos, súmulas e placares (Caskade no BD fará o resto)
        List<Jogo> jogos = jogoRepository.findByProfessorId(idProf);
        jogoRepository.deleteAll(jogos);
        
        // 2. Apaga todos os Alunos inscritos (e as URLs das fotos cadastradas perdem as referências)
        List<Aluno> alunos = alunoRepository.findByProfessorId(idProf);
        alunoRepository.deleteAll(alunos);
        
        // 3. Apaga os Lotes e Modalidades da Arena
        List<Lote> lotes = loteRepository.findByProfessorId(idProf);
        loteRepository.deleteAll(lotes);
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
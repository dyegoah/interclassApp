package br.com.higitech.interclasseApp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.higitech.interclasseApp.dto.LoteSetupDTO;
import br.com.higitech.interclasseApp.model.Lote;
import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.repositories.LoteRepository;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;

@Service
public class SetupService {

    private final LoteRepository loteRepository;
    private final ModalidadeRepository modalidadeRepository;

    // CONSTRUTOR MANUAL (Substitui o Lombok)
    public SetupService(LoteRepository loteRepository, ModalidadeRepository modalidadeRepository) {
        this.loteRepository = loteRepository;
        this.modalidadeRepository = modalidadeRepository;
    }

    @Transactional
    public void processarLote(LoteSetupDTO dto) {
        Lote lote = loteRepository.findByGenero(dto.genero())
                .orElseGet(() -> {
                    Lote novoLote = new Lote();
                    novoLote.setGenero(dto.genero());
                    return loteRepository.save(novoLote);
                });

        if (lote.getModalidades() != null && !lote.getModalidades().isEmpty()) {
            modalidadeRepository.deleteAll(lote.getModalidades());
        }

        List<Modalidade> novasModalidades = dto.esportes().stream().map(espDTO -> {
            Modalidade mod = new Modalidade();
            mod.setLote(lote);
            mod.setFormato(espDTO.formato());
            mod.setQtdTimes(espDTO.qtdTimes());
            mod.setIdaEVolta(espDTO.idaEVolta());
            mod.setNomeEsporte("Esporte ID: " + espDTO.id()); 
            return mod;
        }).toList();

        modalidadeRepository.saveAll(novasModalidades);
    }
}
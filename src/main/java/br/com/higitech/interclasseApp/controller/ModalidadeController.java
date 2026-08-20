package br.com.higitech.interclasseApp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.higitech.interclasseApp.model.Modalidade;
import br.com.higitech.interclasseApp.repositories.ModalidadeRepository;

@RestController
@RequestMapping("/api/modalidades")
public class ModalidadeController {

    @Autowired
    private ModalidadeRepository modalidadeRepository;

    // 🛡️ DTO DE BLINDAGEM ATUALIZADO COM O GÊNERO DA MODALIDADE
    public static class ModalidadePublicaDTO {
        public Long id;
        public String nomeEsporte;
        public String icone;
        public String genero; // 🚀 NOVA VARIÁVEL

        public ModalidadePublicaDTO(Modalidade modalidade) {
            this.id = modalidade.getId();
            this.nomeEsporte = modalidade.getNomeEsporte();
            this.icone = modalidade.getIcone();
            // A Inteligência: Puxa o Gênero do Lote atrelado a este esporte
            this.genero = modalidade.getLote() != null ? modalidade.getLote().getGenero() : "Geral";
        }
    }

    // 🔓 ROTA PÚBLICA: Devolve os esportes usando a chave HASH
    @GetMapping("/public/{professorHash}")
    public ResponseEntity<List<ModalidadePublicaDTO>> listarModalidadesDaEscola(@PathVariable String professorHash) {
        List<Modalidade> modalidades = modalidadeRepository.findByLoteProfessorHashPublico(professorHash);
        List<ModalidadePublicaDTO> modalidadeDTOs = modalidades.stream()
                .map(ModalidadePublicaDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(modalidadeDTOs);
    }
}
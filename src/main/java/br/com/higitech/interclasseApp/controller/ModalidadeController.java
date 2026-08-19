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

    // =========================================================
    // 🛡️ DTO DE BLINDAGEM: Impede o vazamento do Lote/Professor
    // =========================================================
    public static class ModalidadePublicaDTO {
        public Long id;
        public String nomeEsporte;
        public String icone;

        public ModalidadePublicaDTO(Modalidade modalidade) {
            this.id = modalidade.getId();
            this.nomeEsporte = modalidade.getNomeEsporte();
            this.icone = modalidade.getIcone();
            // Apenas os dados que o aluno/aplicativo precisam para desenhar a tela!
        }
    }

    // =========================================================
    // 🔓 ROTA PÚBLICA: Devolve os esportes usando a chave HASH
    // =========================================================
    @GetMapping("/public/{professorHash}")
    public ResponseEntity<List<ModalidadePublicaDTO>> listarModalidadesDaEscola(@PathVariable String professorHash) {
        
        // 1. Busca no banco de forma segura
        List<Modalidade> modalidades = modalidadeRepository.findByLoteProfessorHashPublico(professorHash);
        
        // 2. Converte a Entidade pesada em DTO leve e seguro
        List<ModalidadePublicaDTO> modalidadeDTOs = modalidades.stream()
                .map(ModalidadePublicaDTO::new)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(modalidadeDTOs);
    }
}
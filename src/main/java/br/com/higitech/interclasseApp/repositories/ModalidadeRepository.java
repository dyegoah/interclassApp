package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Modalidade;

@Repository
public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {
    
    List<Modalidade> findByLoteId(Long loteId);

    List<Modalidade> findByLoteProfessorHashPublico(String hashPublico);

    // 🔥 A TRAVA QUE FALTAVA: Busca estrita que cruza o Hash do Professor com o Gênero do Lote
    List<Modalidade> findByLoteProfessorHashPublicoAndLoteGenero(String hashPublico, String genero);
}
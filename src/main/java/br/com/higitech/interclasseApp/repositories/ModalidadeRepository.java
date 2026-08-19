package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Modalidade;

@Repository
public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {
    
    // 🛡️ BLINDAGEM MULTI-TENANT (Uso interno): Isola as modalidades pelo Lote do Professor
    List<Modalidade> findByLoteId(Long loteId);

    // 🔒 BUSCA PÚBLICA BLINDADA: Navega da Modalidade -> Lote -> Professor -> Hash
    List<Modalidade> findByLoteProfessorHashPublico(String hashPublico);
}
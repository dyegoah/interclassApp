package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.higitech.interclasseApp.model.Modalidade;

public interface ModalidadeRepository extends JpaRepository<Modalidade, Long> {
    
    // 🔒 Busca todas as modalidades que pertencem aos lotes do professor ID 1
    List<Modalidade> findByLoteProfessorId(Long professorId);
}
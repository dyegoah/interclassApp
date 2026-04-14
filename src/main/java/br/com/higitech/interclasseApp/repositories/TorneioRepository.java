package br.com.higitech.interclasseApp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.Torneio;

@Repository
public interface TorneioRepository extends JpaRepository<Torneio, Long> {
    // 🔒 Traz apenas os torneios criados por um professor específico!
    List<Torneio> findByProfessorId(Long professorId);
}
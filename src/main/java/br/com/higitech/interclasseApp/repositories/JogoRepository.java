package br.com.higitech.interclasseApp.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.higitech.interclasseApp.model.Jogo;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
    
    // 🔒 TRAVA SAAS: Traz todos os jogos deste professor
    List<Jogo> findByProfessorId(Long professorId);
    
    // 🔒 TRAVA SAAS: Traz os jogos do professor, filtrados pelo gênero (Lote), em ordem cronológica
    List<Jogo> findByProfessorIdAndGeneroOrderByDataJogoAscHorarioAsc(Long professorId, String genero);
    
}
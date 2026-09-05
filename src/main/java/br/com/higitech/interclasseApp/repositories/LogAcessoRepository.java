package br.com.higitech.interclasseApp.repositories;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.higitech.interclasseApp.model.LogAcesso;
import jakarta.transaction.Transactional;

@Repository
public interface LogAcessoRepository extends JpaRepository<LogAcesso, Long> {
	
	@Modifying
    @Transactional
    @Query("DELETE FROM LogAcesso l WHERE l.dataHora < :dataLimite")
    void deleteByDataHoraBefore(@Param("dataLimite") LocalDateTime dataLimite);

}
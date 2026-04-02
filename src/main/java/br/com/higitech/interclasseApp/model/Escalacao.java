package br.com.higitech.interclasseApp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_escalacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Escalacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false, length = 1)
    private String equipe; // "A" ou "B"

    private Integer numeroCamisa;

	public Long getId() {
		return id;
	}

	public Jogo getJogo() {
		return jogo;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public String getEquipe() {
		return equipe;
	}

	public Integer getNumeroCamisa() {
		return numeroCamisa;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setJogo(Jogo jogo) {
		this.jogo = jogo;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public void setNumeroCamisa(Integer numeroCamisa) {
		this.numeroCamisa = numeroCamisa;
	}
    
    
}
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
@Table(name = "tb_evento_sumula")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class EventoSumula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogo_id", nullable = false)
    private Jogo jogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno; // O atleta que realizou a ação

    @Column(nullable = false)
    private String iconeAcao; // ⚽, 🟨, 🟥, ✌️, 🏀

    private String descricao; // "Gol", "Falta Pessoal", "Ace"

    private Integer pontosGerados; // 1 (Gol), 3 (Cesta de 3), 0 (Cartão)

    private String periodoJogo; // "1º Tempo", "Tie-Break"
    
    private String cronometro; // "14:32"

	public Long getId() {
		return id;
	}

	public Jogo getJogo() {
		return jogo;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public String getIconeAcao() {
		return iconeAcao;
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getPontosGerados() {
		return pontosGerados;
	}

	public String getPeriodoJogo() {
		return periodoJogo;
	}

	public String getCronometro() {
		return cronometro;
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

	public void setIconeAcao(String iconeAcao) {
		this.iconeAcao = iconeAcao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setPontosGerados(Integer pontosGerados) {
		this.pontosGerados = pontosGerados;
	}

	public void setPeriodoJogo(String periodoJogo) {
		this.periodoJogo = periodoJogo;
	}

	public void setCronometro(String cronometro) {
		this.cronometro = cronometro;
	}
    
    
}
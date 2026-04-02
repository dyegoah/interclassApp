package br.com.higitech.interclasseApp.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_jogo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo; // Ex: Semifinal, Rodada 1

    private LocalDate dataJogo;
    
    private LocalTime horario;
    
    private String quadra;

    @Column(nullable = false)
    private String status; // PENDENTE, EM_ANDAMENTO, FINALIZADO

    private Integer placarA = 0;
    private Integer placarB = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modalidade_id", nullable = false)
    private Modalidade modalidade;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    private List<Escalacao> escalacoes;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL)
    private List<EventoSumula> eventos;

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public LocalDate getDataJogo() {
		return dataJogo;
	}

	public LocalTime getHorario() {
		return horario;
	}

	public String getQuadra() {
		return quadra;
	}

	public String getStatus() {
		return status;
	}

	public Integer getPlacarA() {
		return placarA;
	}

	public Integer getPlacarB() {
		return placarB;
	}

	public Modalidade getModalidade() {
		return modalidade;
	}

	public List<Escalacao> getEscalacoes() {
		return escalacoes;
	}

	public List<EventoSumula> getEventos() {
		return eventos;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setDataJogo(LocalDate dataJogo) {
		this.dataJogo = dataJogo;
	}

	public void setHorario(LocalTime horario) {
		this.horario = horario;
	}

	public void setQuadra(String quadra) {
		this.quadra = quadra;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setPlacarA(Integer placarA) {
		this.placarA = placarA;
	}

	public void setPlacarB(Integer placarB) {
		this.placarB = placarB;
	}

	public void setModalidade(Modalidade modalidade) {
		this.modalidade = modalidade;
	}

	public void setEscalacoes(List<Escalacao> escalacoes) {
		this.escalacoes = escalacoes;
	}

	public void setEventos(List<EventoSumula> eventos) {
		this.eventos = eventos;
	}
    
    
}


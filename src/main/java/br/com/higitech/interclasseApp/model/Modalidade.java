package br.com.higitech.interclasseApp.model;

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
@Table(name = "tb_modalidade")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Modalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeEsporte; // Futsal, Vôlei, Basquete

    private String icone; // ⚽, 🏐

    private String formato; // eliminatoria, liga, grupos
    
    private Integer qtdTimes;
    
    private Boolean idaEVolta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @OneToMany(mappedBy = "modalidade", cascade = CascadeType.ALL)
    private List<Jogo> jogos;

	public Long getId() {
		return id;
	}

	public String getNomeEsporte() {
		return nomeEsporte;
	}

	public String getIcone() {
		return icone;
	}

	public String getFormato() {
		return formato;
	}

	public Integer getQtdTimes() {
		return qtdTimes;
	}

	public Boolean getIdaEVolta() {
		return idaEVolta;
	}

	public Lote getLote() {
		return lote;
	}

	public List<Jogo> getJogos() {
		return jogos;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNomeEsporte(String nomeEsporte) {
		this.nomeEsporte = nomeEsporte;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public void setQtdTimes(Integer qtdTimes) {
		this.qtdTimes = qtdTimes;
	}

	public void setIdaEVolta(Boolean idaEVolta) {
		this.idaEVolta = idaEVolta;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
	}

	public void setJogos(List<Jogo> jogos) {
		this.jogos = jogos;
	}
    
    
}
package br.com.higitech.interclasseApp.model;

import jakarta.persistence.Entity;
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
@Table(name = "tb_classificacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = "id")
public class Classificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeTurma; // Ex: "3º Ano A"

    // Relacionamento: Essa linha de classificação pertence a qual Modalidade/Lote?
    @ManyToOne
    @JoinColumn(name = "modalidade_id")
    private Modalidade modalidade;

    // --- DADOS PADRÃO DA ESPN ---
    private Integer pontos = 0;          // PTS
    private Integer jogosRealizados = 0; // J
    private Integer vitorias = 0;        // V
    private Integer empates = 0;         // E
    private Integer derrotas = 0;        // D
    private Integer golsPro = 0;         // GP
    private Integer golsContra = 0;      // GC
    private Integer saldoGols = 0;       // SG
    private Integer aproveitamento = 0;  // % (Opcional, mas fica legal)
    
    // --- GETTERS E SETTERS MANUAIS (Para evitar aquele erro da IDE) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeTurma() { return nomeTurma; }
    public void setNomeTurma(String nomeTurma) { this.nomeTurma = nomeTurma; }

    public Modalidade getModalidade() { return modalidade; }
    public void setModalidade(Modalidade modalidade) { this.modalidade = modalidade; }

    public Integer getPontos() { return pontos; }
    public void setPontos(Integer pontos) { this.pontos = pontos; }

    public Integer getJogosRealizados() { return jogosRealizados; }
    public void setJogosRealizados(Integer jogosRealizados) { this.jogosRealizados = jogosRealizados; }

    public Integer getVitorias() { return vitorias; }
    public void setVitorias(Integer vitorias) { this.vitorias = vitorias; }

    public Integer getEmpates() { return empates; }
    public void setEmpates(Integer empates) { this.empates = empates; }

    public Integer getDerrotas() { return derrotas; }
    public void setDerrotas(Integer derrotas) { this.derrotas = derrotas; }

    public Integer getGolsPro() { return golsPro; }
    public void setGolsPro(Integer golsPro) { this.golsPro = golsPro; }

    public Integer getGolsContra() { return golsContra; }
    public void setGolsContra(Integer golsContra) { this.golsContra = golsContra; }

    public Integer getSaldoGols() { return saldoGols; }
    public void setSaldoGols(Integer saldoGols) { this.saldoGols = saldoGols; }

    public Integer getAproveitamento() { return aproveitamento; }
    public void setAproveitamento(Integer aproveitamento) { this.aproveitamento = aproveitamento; }
}
package br.com.higitech.interclasseApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_modalidade")
public class Modalidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeEsporte;
    private String icone;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    // ==========================
    // GETTERS E SETTERS
    // ==========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeEsporte() { return nomeEsporte; }
    public void setNomeEsporte(String nomeEsporte) { this.nomeEsporte = nomeEsporte; }

    public String getIcone() { return icone; }
    public void setIcone(String icone) { this.icone = icone; }

    public Lote getLote() { return lote; }
    public void setLote(Lote lote) { this.lote = lote; }
}
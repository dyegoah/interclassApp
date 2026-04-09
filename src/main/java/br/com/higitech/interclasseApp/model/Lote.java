package br.com.higitech.interclasseApp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String genero; // Masculino, Feminino ou Misto

    // 🌟 A MÁGICA MULTI-TENANT AQUI: Quem é o dono deste lote de categorias?
    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    // ==========================
    // GETTERS E SETTERS
    // ==========================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public Professor getProfessor() { return professor; }
    public void setProfessor(Professor professor) { this.professor = professor; }
}
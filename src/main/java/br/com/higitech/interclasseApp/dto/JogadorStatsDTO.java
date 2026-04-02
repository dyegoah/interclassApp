package br.com.higitech.interclasseApp.dto;

import java.util.List;

public record JogadorStatsDTO(
    String id, 
    Integer numero, 
    String nome, 
    String foto, 
    List<String> stats // A lista de ícones/pílulas (ex: ["⚽", "🟨", "⚽"])
) {}
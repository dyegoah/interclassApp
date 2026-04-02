package br.com.higitech.interclasseApp.dto;

public record JogoDTO(
    Long id, 
    String diaId, 
    String hora, 
    String esporte, 
    String icone, 
    String titulo, 
    String quadra
) {}
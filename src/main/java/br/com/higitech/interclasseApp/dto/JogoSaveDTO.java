package br.com.higitech.interclasseApp.dto;

public record JogoSaveDTO(
    Long id,
    String diaId,
    String hora,
    String esporte,
    String icone,
    String titulo,
    String quadra,
    Long equipeAId,
    String equipeANome,
    Long equipeBId,
    String equipeBNome
) {
}
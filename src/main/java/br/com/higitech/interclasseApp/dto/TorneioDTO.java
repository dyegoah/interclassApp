package br.com.higitech.interclasseApp.dto;

import java.time.LocalDate;

public record TorneioDTO(
    String nome,
    Integer ano,
    LocalDate dataInicio,
    LocalDate dataFim
) {}
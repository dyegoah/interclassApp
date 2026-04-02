package br.com.higitech.interclasseApp.dto;

import java.util.List;
import java.util.Map;

public record SumulaFinalizadaDTO(
    PlacarDTO placar,
    Integer periodoIndex,
    Map<String, List<JogadorStatsDTO>> escalacao, // Mapeia a { "A": [...], "B": [...] }
    List<TimelineDTO> timeline
) {}
package br.com.higitech.interclasseApp.dto;

public record EsporteSetupDTO(
    Integer id, 
    String formato, 
    Integer qtdTimes, 
    Boolean idaEVolta
) {}
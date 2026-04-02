package br.com.higitech.interclasseApp.dto;

public record ModalidadeSetupDTO(
    Long id, 
    String formato, 
    Integer qtdTimes, 
    Boolean idaEVolta
) {}
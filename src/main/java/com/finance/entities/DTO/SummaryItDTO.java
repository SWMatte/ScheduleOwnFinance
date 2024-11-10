package com.finance.entities.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finance.entities.Type;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SummaryItDTO {

    @JsonProperty("RegistroEventiId")
    private int RegistroEventiId;

    @JsonProperty("Descrizione")
    private String Descrizione;

    @JsonProperty("Data")
    private LocalDate Data;

    @JsonProperty("TipoEvento")
    private Type TipoEvento;

    @JsonProperty("ValoreInserito")
    private Double ValoreInserito;

    @JsonProperty("EuroRisparmiati")
    private Double EuroRisparmiati;

    @JsonProperty("EuroDisponibili")
    private String EuroDisponibili;

    @JsonProperty("PercentualeRisparmio")
    private Double PercentualeRisparmio;



}

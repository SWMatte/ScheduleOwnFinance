package com.finance.entities.DTO;

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


    private int RegistroEventiId;


    private String Descrizione;


    private LocalDate Data;


    @Enumerated(EnumType.STRING)
    private Type TipoEvento;

    private Double Valore;

    private Double EuroRisparmiati;

    private String EuroDisponibili;

    private Double PercentualeRisparmio;



}

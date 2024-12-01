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

     private int registroEventiId;

     private String descrizione;

     private LocalDate data;

     private Type tipoEvento;

     private Double valoreInserito;

     private Double euroRisparmiati;

     private String euroDisponibili;

     private Double percentualeRisparmio;



}

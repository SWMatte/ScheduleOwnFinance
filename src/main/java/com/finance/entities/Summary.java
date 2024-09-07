package com.finance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "Riepilogo")
public class Summary {

    @Id
    @Column(name = "Registro_eventi_id")
    private int summaryId;


    @Column(name = "Descrizione")
    private String description;


    @Column(name = "Data")
    private LocalDate data;

    @Column(name = "Tipo_evento")
    @Enumerated(EnumType.STRING)
    private Type typeEvent;

    @Column(name = "Valore_inserito")
    private Double value;

    @Column(name = "Euro_risparmiati")
    private Double euroSaved;

    @Column(name = "Euro_disponibili")
    private String euroAvailable;

    @Column(name = "Percentuale_risparmio")
    private Double percentageSaved;




}

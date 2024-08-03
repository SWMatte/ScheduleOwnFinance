package com.finance.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;

@Table(name = "Registro_Eventi")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class EventRegistration {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;
    private LocalDate data;
    private String description;
    private String value;//valore in euro
    private String percentageSaveMoney; // percentuale per risparmiare
    private boolean savedMoney;  // booleano per risparmiare o meno la somma in ingresso
    private boolean objective; // booleano per obiettivo previssato da scalare soldi
    @Enumerated(EnumType.STRING)
    private Type typeEvent; // tipo di evento entrata - spesa
}

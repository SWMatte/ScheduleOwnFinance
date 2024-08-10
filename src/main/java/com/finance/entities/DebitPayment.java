package com.finance.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "Debito_Rateizzato")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class DebitPayment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Debito_ID")
    private int debitID;
    private LocalDate data;
    @Column(name = "descrizione")
    private String description;
    @Column(name = "valore_iniziale")
    private Double valueStart;//valor
    @Column(name = "valore_corrente")
    private Double valueFinish;
    @Column(name = "debito_saldato")
    private Boolean settled = false;  // quando il debito è ultimato non lo vedremo +


}

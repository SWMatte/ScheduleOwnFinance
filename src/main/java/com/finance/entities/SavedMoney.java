package com.finance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Table(name = "Totale_risparmiato")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class SavedMoney {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "euro_risparmiati_ID")
    private int idSavedMoney;


    @Column(name = "Euro_Risparmiati")
    private Double moneySaved;

    @ManyToOne
    @JoinColumn(name = "Registro_eventi_ID")
    private EventRegistration eventRegistration;

    private LocalDate data;

    @OneToOne(mappedBy = "savedMoney")
    private HandleMoney handleMoney;

}

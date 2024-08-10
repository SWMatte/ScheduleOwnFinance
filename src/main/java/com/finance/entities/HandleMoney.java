package com.finance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "Gestione_spese")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class HandleMoney {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "Euro_disponibili")
    private Double euroDisponibili;


    @ManyToOne
    @JoinColumn(name = "Registro_eventi_ID")
    private EventRegistration eventRegistration;

    @OneToOne
    @JoinColumn(name = "euro_risparmiati_ID")
    private SavedMoney savedMoney;


}

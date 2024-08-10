package com.finance.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "Debito_Rateizzato_History")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class DebitPaymentHistory {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Debito_history_ID")
    private int debitHistoryID;

    @Column(name = "euro_dedicati")
    private Double value;

    @ManyToOne()
    @JoinColumn(name = "Debito_ID")
    DebitPayment debitPayment;
}

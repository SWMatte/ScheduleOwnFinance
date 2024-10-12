package com.finance.entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitsDTO {

    private LocalDate dataInserimento;
    private String descrizione;
    private Boolean debitoSaldato;
    private Double saldoResiduo;
    private Double valoreDebito;
    private Double euroDedicati;
}

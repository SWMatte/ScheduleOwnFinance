package com.finance.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pdf")
     private int idPdf;

    @Lob
    @Column(name = "pdf_data")
     private byte[] pdfData;

    @Column(name = "number_order")
     private String numberOrder;

    @Column(name = "document_processed")
     private boolean documentProcessed;

    @Column(name = "date_saved")
     private LocalDate dateSaved;


}
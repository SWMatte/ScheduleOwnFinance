package com.finance.entities;

import com.finance.entities.Auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
    @Column(name = "pdf_data", columnDefinition = "LONGBLOB")
     private byte[] pdfData;



    @Column(name = "document_processed")
     private boolean documentProcessed;

    @Column(name = "date_saved")
     private Date dateSavedPdf;


    @Column(name = "reference_month")
    private String referenceMonth;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
}
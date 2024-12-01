package com.finance.services;

import com.finance.entities.Auth.User;
import com.finance.entities.DTO.PdfDTO;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;

public interface iPdfOperation {


    PDDocument createCustomPdf(String outputPdfPath, PdfDTO fields, User user) throws IOException;
}

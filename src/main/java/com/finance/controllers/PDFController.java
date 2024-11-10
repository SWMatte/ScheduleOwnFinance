package com.finance.controllers;


import com.finance.entities.DTO.PdfDTO;
import com.finance.services.PdfService;
import com.finance.utils.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
 import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/")
@RestController
public class PdfController extends BaseService<String> {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<InputStreamResource> generatePdf(@RequestBody PdfDTO requestDto) throws IOException {
        String outputPdfPath = "src/main/resources/generated_invoice.pdf";


        // Chiama il servizio per generare il PDF
        pdfService.createCustomPdf(outputPdfPath, requestDto);

        // Ritorna il PDF generato come risposta
        InputStreamResource resource = new InputStreamResource(new FileInputStream(outputPdfPath));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }



}

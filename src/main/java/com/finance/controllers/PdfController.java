package com.finance.controllers;


import com.finance.entities.Auth.Authorized;
import com.finance.entities.Auth.Role;
import com.finance.entities.Auth.User;
import com.finance.entities.DTO.PdfDTO;
import com.finance.services.PdfService;
import com.finance.utils.BaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    @Authorized(roles = {Role.ADMIN})
    public ResponseEntity<?> generatePdf(@RequestBody PdfDTO requestDto, User user) throws IOException {
        String outputPdfPath = "src/main/resources/generated_invoice.pdf";
        // Chiama il servizio per generare il PDF
        pdfService.createCustomPdf(outputPdfPath, requestDto,user);
        // Ritorna il PDF generato come risposta
         return ResponseEntity.ok().body("Pdf created correctly");
    }





    @PostMapping("/retrievePdf")
    @Authorized(roles = {Role.ADMIN})
    public ResponseEntity<?> retrievePdf(User user,@RequestParam String month) {
        log.info("Starting method retrievePdf in class: " + getClass());
        try {
            PDDocument pdDocument = pdfService.constructPdfFromDatabase(user, month);
            return ResponseEntity.ok().body("Pdf retrieved correctly");
        } catch (EntityNotFoundException e) {
            log.error("Can't retrieve the pdf : " + e.getMessage());
            return ResponseEntity.internalServerError().body(HttpStatus.BAD_REQUEST +" "+ e.getMessage());

        }
    }

}

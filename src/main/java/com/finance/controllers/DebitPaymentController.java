package com.finance.controllers;

import com.finance.entities.DebitPayment;
import com.finance.services.iDebitPayment;
import com.finance.utils.BaseService;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1")
@RestController
public class DebitPaymentController extends BaseService<DebitPayment> {

    @Autowired
    private final iDebitPayment iDebitPayment;

    @PostMapping("addDebts")
    public ResponseEntity<?> addElement(@RequestBody DebitPayment debitPayment) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        try {
            iDebitPayment.addElement(debitPayment);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error into: " + getCurrentClassName() + "method: " + getCurrentMethodName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }


    }

// TODO: AGGIUNGERE LA SP PER GESTIRE I DEBITI TRAMITE L'EVENTO COME DESCRITTO SUL WORD, PROVARE A FARE UN GIRO COMPLETO
   // TODO: AGGIUNGERE FEATURE LEGATA ALL'AUTENTIFICAZIONE FARE ANCHE NELL'ALTRO PROGETTO
    @GetMapping("getListDebts")
    public ResponseEntity<?> getListDebts() {
        try {
            List<DebitPayment> listDebts = iDebitPayment.visualizeAvailable();
            return ResponseEntity.ok().body(listDebts);
        } catch (EntityExistsException e) {
            log.error("Error into: " + getCurrentClassName() + "method: " + getCurrentMethodName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }

    }
}

package com.finance.controllers;

import com.finance.entities.DTO.EventRegistrationDTO;
import com.finance.entities.DTO.FinanceDTO;
import com.finance.entities.DebitPayment;
import com.finance.entities.Summary;
import com.finance.services.SummaryService;
import com.finance.services.iDebitPayment;
import com.finance.utils.BaseService;
import com.finance.utils.ExceptionCustom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/")
@RestController
public class SummaryController extends BaseService<DebitPayment> {

    @Autowired
    private final SummaryService summaryService;


    @GetMapping("getList")
    public ResponseEntity<?> getListDebts() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        try {
            List<Summary> listDebts = summaryService.getAllValues();
            return ResponseEntity.ok().body(listDebts);

        } catch (ExceptionCustom e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("getFinance")
    public ResponseEntity<?> getFinance() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
            FinanceDTO finance = summaryService.getFinance();
            return ResponseEntity.ok().body(finance);

    }




}

package com.finance.controllers;

import com.finance.entities.Auth.Authorized;
import com.finance.entities.Auth.Role;
import com.finance.entities.DTO.EventRegistrationDTO;
import com.finance.entities.DTO.FinanceDTO;
import com.finance.entities.DTO.SummaryItDTO;
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
    @Authorized(roles = {Role.ADMIN})
    public ResponseEntity<?> getListDebts(@RequestParam String month) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        if(month.isEmpty() || month.isBlank()){
            month=null;
        }
        try {
            List<SummaryItDTO> listDebts = summaryService.getAllValues(month);
            return ResponseEntity.ok().body(listDebts);
        } catch (ExceptionCustom e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("getFinance")
    @Authorized(roles = {Role.ADMIN})
    public ResponseEntity<?> getFinance() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
            FinanceDTO finance = summaryService.getFinance();
            return ResponseEntity.ok().body(finance);
    }
}

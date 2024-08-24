package com.finance.controllers;

import com.finance.entities.DebitPayment;
import com.finance.entities.Summary;
import com.finance.services.SummaryService;
import com.finance.services.iDebitPayment;
import com.finance.utils.BaseService;
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



     @GetMapping("getAllList")
    public ResponseEntity<?> getListDebts() {

        List<Summary> listDebts = summaryService.getAllValues();
        return ResponseEntity.ok().body(listDebts);


    }


}

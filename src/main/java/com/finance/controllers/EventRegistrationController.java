package com.finance.controllers;

import com.finance.entities.DTO.EventRegistrationDTO;
import com.finance.services.iElement;
import com.finance.utils.BaseService;
import com.finance.utils.ExceptionCustom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
@Data
@Slf4j
public class EventRegistrationController extends BaseService<EventRegistrationDTO> {

    @Autowired
    private final iElement<EventRegistrationDTO> eventRegistrationService;


    @PostMapping("addEvent")
    public ResponseEntity<?> addElement(@RequestBody EventRegistrationDTO eventRegistration) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());

        if (!isNullValue(eventRegistration)) {
            try {
                eventRegistrationService.addElement(eventRegistration);
                return ResponseEntity.status(HttpStatus.OK).build();
            } catch (ExceptionCustom | RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Parameter in inputs are empty");
        }
    }


    @GetMapping("getMoney")
    public Double getMoneyAvailable() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        return eventRegistrationService.visualizeAvailable();
    }


}

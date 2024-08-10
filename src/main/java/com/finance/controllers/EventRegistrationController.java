package com.finance.controllers;

import com.finance.entities.DTO.EventRegistrationDTO;
import com.finance.entities.EventRegistration;
import com.finance.services.EventRegistrationService;
import com.finance.services.iElement;
import com.finance.utils.BaseService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
@Data
@Slf4j
public class EventRegistrationController extends BaseService<EventRegistrationDTO> {

    @Autowired
    private final iElement<EventRegistrationDTO> EventRegistrationService;



    @PostMapping("addEvent")
    public void addElement(@RequestBody EventRegistrationDTO eventRegistration){
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        if(!isNullValue(eventRegistration)){
            EventRegistrationService.addElement(eventRegistration);
        }

    }


    @GetMapping("getMoney")
    public Double getMoneyAvailable(){
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
          return  EventRegistrationService.visualizeAvailable();
        }


}

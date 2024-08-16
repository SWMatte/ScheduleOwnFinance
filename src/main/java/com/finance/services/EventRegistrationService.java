package com.finance.services;

import com.finance.entities.DTO.EventRegistrationDTO;
import com.finance.entities.EventRegistration;
import com.finance.repositories.EventRegistrationRepository;
import com.finance.utils.BaseService;
import com.finance.utils.ExceptionCustom;
import jdk.jfr.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@AllArgsConstructor
@Slf4j
@Service
public class EventRegistrationService extends BaseService implements iElement<EventRegistrationDTO> {

    @Autowired
    private final EventRegistrationRepository eventRegistrationRepository;

    @Override
    public void addElement(EventRegistrationDTO element) throws ExceptionCustom {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        if (!isNullValue(element)) {
            EventRegistration eventRegistration = EventRegistration.builder()
                    .data(LocalDate.now())
                    .description(element.getDescription())
                    .value(element.getValue())
                    .percentageSaveMoney(element.getPercentageSaveMoney())
                    .savedMoney(element.isSavedMoney())
                    .objective(element.isObjective())
                    .typeEvent(element.getTypeEvent())
                    .build();
            eventRegistrationRepository.save(eventRegistration);

            eventRegistrationRepository.converter();
            log.info("Execute : " + " converter SP");
            log.info("Finish method: " + getCurrentMethodName());


        } else {
            log.error("Error into " + getCurrentClassName());
            throw new RuntimeException("Problem with " + element + " value is null");
        }

    }

    @Override
    public Double visualizeAvailable() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        Double money = eventRegistrationRepository.moneyAvailable();

        if (!isNullValue(money)) {
            return money;

        } else {
            log.error("Error into " + getCurrentClassName());
            throw new RuntimeException("Problem with " + money + " value is null");
        }
    }



}

package com.finance.services;

import com.finance.entities.DTO.FinanceDTO;
import com.finance.entities.DTO.SummaryItDTO;
import com.finance.entities.Summary;
import com.finance.repositories.SummaryRepository;
import com.finance.utils.BaseService;
import com.finance.utils.ExceptionCustom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SummaryService extends BaseService {

    @Autowired
    private final SummaryRepository summaryRepository;


    public List<SummaryItDTO> getAllValues(String month) throws ExceptionCustom {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        List<SummaryItDTO> listSummary = null;
        if (isNullValue(month)) {
            log.info("Finish method: " + getCurrentMethodName() + "not filtered");
            return listSummary = summaryRepository.findAllElements();

        } else {
            log.info("Finish method: " + getCurrentMethodName() + " filtered ");
            return listSummary = summaryRepository.findSpecificElements(month);
        }

    }


    public FinanceDTO getFinance() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        Double value = summaryRepository.getFinance();
        FinanceDTO finance = FinanceDTO.builder().actualFinance(value).build();
        log.info("Finish method: " + getCurrentMethodName());
        return finance;

    }


}

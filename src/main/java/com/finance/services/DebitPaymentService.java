package com.finance.services;

import com.finance.entities.DTO.DebitsDTO;
import com.finance.entities.DebitPayment;
import com.finance.repositories.DebitPaymentRepository;
import com.finance.utils.BaseService;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Data
public class DebitPaymentService extends BaseService implements iDebitPayment {

    @Autowired
    private final DebitPaymentRepository debitPaymentRepository;

    @Override
    public void addElement(DebitPayment element) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        if (isNullValue(debitPaymentRepository.findByDescriptionAndValueStart(element.getDescription(), element.getValueStart()))) {
            if (!isNullValue(element)) {
                element.setValueFinish(element.getValueStart());
                element.setData(LocalDate.now());
                debitPaymentRepository.save(element);

            } else {
                log.error("Error into " + getCurrentClassName());
                throw new RuntimeException("Problem with " + element + " value is null");
            }


        } else {
            log.error("Error into " + getCurrentClassName());
            throw new RuntimeException("This debit is already exist " + element.getDescription());
        }

    }

    @Override
    public List<DebitPayment> visualizeAvailable() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        // update debit when is completed issue with lock on db
        debitPaymentRepository.disableSafeUpdates();
        debitPaymentRepository.updateSettledTrue();
        debitPaymentRepository.enableSafeUpdates();



         if (!debitPaymentRepository.listCurrentDebts().isEmpty()) {
            return debitPaymentRepository.listCurrentDebts();
        }else {
            log.info("Non esistono debiti a DB");
            return new ArrayList<>();
        }

     }

    @Override
    public void reduceDebit(int idDebit) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        if (idDebit > 0) {
            log.info("CALL procedure handleDebit(gestione_debito)");
            debitPaymentRepository.handleDebit(idDebit);
            log.info("Finish method: " + getCurrentMethodName());

        }
    }

    @Override
    public List<DebitPayment> visualizeCompletedDebts() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        return debitPaymentRepository.ListCompletedDebit();
    }

    @Override
    public DebitsDTO visualizeMoreInfoDebts(String debitoID) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        return debitPaymentRepository.moreInfoDebts(debitoID);
    }

    @Override
    public Double numbersOfTotalDebts() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        return debitPaymentRepository.numbersOfTotalDebts();

    }

    @Override
    public Double numbersOfFinishDebts() {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        return debitPaymentRepository.numbersOfFinishDebts();

    }


}

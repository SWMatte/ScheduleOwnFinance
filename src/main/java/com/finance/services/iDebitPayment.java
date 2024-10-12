package com.finance.services;

import com.finance.entities.DTO.DebitsDTO;
import com.finance.entities.DebitPayment;

import java.util.List;

public interface iDebitPayment {

     void addElement(DebitPayment element);

     List<DebitPayment> visualizeAvailable();

     void reduceDebit(int idDebit);


     List<DebitPayment> visualizeCompletedDebts();

     DebitsDTO visualizeMoreInfoDebts(String debitoID);

     Double numbersOfTotalDebts();

     Double numbersOfFinishDebts();



}

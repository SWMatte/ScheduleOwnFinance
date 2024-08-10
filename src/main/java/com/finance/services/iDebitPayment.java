package com.finance.services;

import com.finance.entities.DebitPayment;

import java.util.List;

public interface iDebitPayment {

     void addElement(DebitPayment element);

     List<DebitPayment> visualizeAvailable();

}

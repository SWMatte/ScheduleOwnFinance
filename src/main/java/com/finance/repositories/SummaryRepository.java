package com.finance.repositories;

import com.finance.entities.EventRegistration;
import com.finance.entities.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.transaction.annotation.Transactional;

public interface SummaryRepository extends JpaRepository<Summary,Integer> {


    @Procedure("Finanza_disponibile")
    @Transactional
    Double getFinance();

}

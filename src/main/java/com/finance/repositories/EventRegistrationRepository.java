package com.finance.repositories;

import com.finance.entities.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.transaction.annotation.Transactional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration,Integer> {

    @Transactional
    @Procedure
    void converter();

    @Procedure("Finanza_disponibile")
    @Transactional
    Double moneyAvailable();



}

package com.finance.repositories;

import com.finance.entities.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration,Integer> {


    @Procedure
    void converter();

    @Procedure("Finanza_disponibile")
    Double moneyAvailable();

}

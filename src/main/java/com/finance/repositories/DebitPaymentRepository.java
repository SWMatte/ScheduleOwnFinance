package com.finance.repositories;

import com.finance.entities.DebitPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebitPaymentRepository extends JpaRepository<DebitPayment,Integer> {


     DebitPayment findByDescriptionAndValueStart(String description, Double valueStart);


    @Query("""
            SELECT deb
            FROM DebitPayment deb
            WHERE deb.settled = false
            """)
    List<DebitPayment> listCurrentDebts();
}

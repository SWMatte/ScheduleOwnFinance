package com.finance.repositories;

import com.finance.entities.DebitPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DebitPaymentRepository extends JpaRepository<DebitPayment,Integer> {


     DebitPayment findByDescriptionAndValueStart(String description, Double valueStart);


    @Query("""
            SELECT deb
            FROM DebitPayment deb
            WHERE deb.settled = false
            """)
    List<DebitPayment> listCurrentDebts();


    @Modifying
    @Transactional
    @Query(value = "SET SQL_SAFE_UPDATES = 0", nativeQuery = true)
    void disableSafeUpdates();

    @Modifying
    @Transactional
    @Query(value = "UPDATE Debito_Rateizzato SET debito_saldato = true WHERE valore_corrente = 0", nativeQuery = true)
    void updateSettledTrue();

    @Modifying
    @Transactional
    @Query(value = "SET SQL_SAFE_UPDATES = 1", nativeQuery = true)
    void enableSafeUpdates();

    @Procedure("gestione_debito")
    void handleDebit(int debitId);

}

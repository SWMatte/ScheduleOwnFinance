package com.finance.repositories;

import com.finance.entities.DTO.SummaryItDTO;
import com.finance.entities.EventRegistration;
import com.finance.entities.Summary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SummaryRepository extends JpaRepository<Summary,Integer> {


    @Procedure("Finanza_disponibile")
    @Transactional
    Double getFinance();


    @Query("""
        SELECT NEW com.finance.entities.DTO.SummaryItDTO(
        s.summaryId,
        s.description,
        s.data,
        s.typeEvent,
        s.value,
        s.euroSaved,
        s.percentageSaved,
        s.debit
        )
        FROM Summary s
        """)
    List<SummaryItDTO> findAllElements();
}

package com.finance.repositories;

import com.finance.entities.Auth.User;
import com.finance.entities.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PdfRepository extends JpaRepository<Pdf,Integer> {

 Pdf findByUserAndReferenceMonth(User user,String month);

 }

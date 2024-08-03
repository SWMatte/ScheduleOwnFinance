package com.finance.repositories;

import com.finance.entities.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration,Integer> {
}

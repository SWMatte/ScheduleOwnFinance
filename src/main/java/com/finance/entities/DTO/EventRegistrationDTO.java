package com.finance.entities.DTO;

import com.finance.entities.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRegistrationDTO {


    private String description;
    private String value;
    private String percentageSaveMoney;
    private boolean savedMoney;
    private boolean objective;
    private Type typeEvent;
}

package com.finance.entities.Auth;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "User_id")
    private int userId;

    private String email;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private Role userRole;

    private String password;

}

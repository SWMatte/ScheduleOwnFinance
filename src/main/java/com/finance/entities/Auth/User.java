package com.finance.entities.Auth;


import com.finance.entities.Pdf;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private int userId;

    private String email;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private Role userRole;

    private String password;

    @OneToMany(mappedBy = "user")
    private List<Pdf> pdf;

}

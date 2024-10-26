package com.finance.services;

import com.finance.entities.Auth.AuthenticationRequest;
import com.finance.entities.Auth.AuthenticationResponse;
import com.finance.entities.Auth.RegisterResponse;
import com.finance.entities.Auth.User;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface iUserService {
    User findById(int id);

    Optional<User> findByEmail(String email);

    RegisterResponse saveUser(User user);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}

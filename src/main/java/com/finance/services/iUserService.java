package com.finance.services;

import com.finance.entities.Auth.AuthenticationRequest;
import com.finance.entities.Auth.AuthenticationResponse;
import com.finance.entities.Auth.RegisterResponse;
import com.finance.entities.Auth.User;
import com.finance.utils.ExceptionCustom;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface iUserService {
    User findById(int id);

    Optional<User> findByEmail(String email);

    RegisterResponse saveUser(User user) throws ExceptionCustom;

    AuthenticationResponse authenticate(AuthenticationRequest request) throws ExceptionCustom;

    String changePassword ( String newPassword, User user);
}

package com.finance.services;

import com.finance.entities.Auth.AuthenticationRequest;
import com.finance.entities.Auth.AuthenticationResponse;
import com.finance.entities.Auth.RegisterResponse;
import com.finance.entities.Auth.User;
import com.finance.repositories.UserRepository;
import com.finance.services.jwt.JwtService;
import com.finance.utils.BaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService extends BaseService implements iUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Override
    public User findById(int id) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        log.info("Finish method: " + getCurrentMethodName());
        return user;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        return Optional.ofNullable(userRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public RegisterResponse saveUser(User user) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            userRepository.save(user);
            log.info("Finished method " + getCurrentMethodName());
        } catch (Exception e) {
            log.info("Error save method " + getCurrentMethodName());
            return RegisterResponse.builder().success(false).build();
        }
        return RegisterResponse.builder().success(true).build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Bad credential" + request.getPassword());
        }
        log.info("Calling method Generate Token in JwtService");
        var jwtToken = jwtService.generateToken(user);
        log.info("The token is generated and the user is authenticated!");
        log.info(String.valueOf(user));
        return AuthenticationResponse.builder().token(jwtToken).message("Login successful!").build();
    }


    @Override
    public String changePassword(String newPassword, User user) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        String oldPassword = user.getPassword();
        Optional<User> userDatabase = userRepository.findById(user.getUserId());
        return  userDatabase.filter(userDb -> (userDb.getPassword().equals(oldPassword))).map(userDb -> {
            userDb.setPassword(newPassword);
            log.info("password changed");
            userRepository.save(userDb);
            log.info("password saved correctly");
            return "Password changed";
        }).orElse("Issue with change and saved the new password" + newPassword);
    }


}

package com.finance.controllers;

import com.finance.entities.Auth.*;
import com.finance.entities.DTO.DebitsDTO;
import com.finance.entities.DebitPayment;
import com.finance.services.iDebitPayment;
import com.finance.services.iUserService;
import com.finance.utils.BaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/v1/")
@RestController
public class AuthenticationController extends BaseService<String> {

    @Autowired
    private final iUserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User requester) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        try {
            return ResponseEntity.ok(userService.saveUser(requester));
        } catch (RuntimeException e) {
            log.error("Error into: " + getCurrentClassName() + "method: " + getCurrentMethodName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest requester) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        try {
            return ResponseEntity.ok(userService.authenticate(requester));
        } catch (RuntimeException e) {
            log.error("Error into: " + getCurrentClassName() + "method: " + getCurrentMethodName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PostMapping("changePassword")
    @Authorized(roles = {Role.ADMIN})
    public ResponseEntity<?> changePassword(@RequestParam String password,User user) {
        log.info("Enter into: " + getCurrentClassName() + " start method: " + getCurrentMethodName());
        try {
            if(!isNullValue(password)){
                return ResponseEntity.ok(userService.changePassword(password,user));
            }else{
                throw new RuntimeException("Password input is empty");
            }
        } catch (RuntimeException e) {
            log.error("Error into: " + getCurrentClassName() + "method: " + getCurrentMethodName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



}

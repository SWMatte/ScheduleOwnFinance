package com.finance.entities.Auth;

import com.finance.services.UserService;
import com.finance.services.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Aspect
@Component
@Order(0)
@Slf4j
@AllArgsConstructor
public class AuthenticationAspect {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    HttpServletRequest request;


    @Pointcut("@annotation(Authorized)")
    private void AuthenticateMethod() {
    }


    @Around("AuthenticateMethod()")
    public Object AuthenticationMethod(ProceedingJoinPoint joinPoint ) throws Throwable {
        Authorized auth = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Authorized.class);
        log.info("Inizio dell'aspect : " +getClass() );
        log.info("Prendo la richiesta dall header");
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder().message("Token is missing").build());
        }
        var jwt = authHeader.substring(7); //substring per escludere bearer più uno spazio "bearer "
        if(!jwtService.isTokenValid(jwt)){
            log.error("The Token is not valid. Verified in class: " + getClass());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder().message("Token not valid!").build());
        }
        log.info("Il token è valido!");
        log.info("Extracting the id from the token");
        int id = Integer.parseInt(jwtService.extractStringId(jwt));
        log.info("Finding the user with the extracted id!");
        User userById = null;
        try{
            userById = userService.findById(id);
            if(!Arrays.asList(auth.roles()).isEmpty() && !Arrays.stream(auth.roles()).toList().contains(userById.getUserRole())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AuthenticationResponse.builder().message("User role not match").build());
            }
            log.info("Ho trovato l'user!");
        }catch(NullPointerException e){
            log.error("User not found!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder().message("User not found").build());
        }
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                args[i] = userById;
            }
        }
        log.info("Passo l'user al controller");
        return joinPoint.proceed(args);
    }


}

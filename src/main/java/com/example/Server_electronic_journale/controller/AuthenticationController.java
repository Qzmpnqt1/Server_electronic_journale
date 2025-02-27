package com.example.Server_electronic_journale.controller;

import com.example.Server_electronic_journale.dto.AuthRequest;
import com.example.Server_electronic_journale.dto.AuthResponse;
import com.example.Server_electronic_journale.dto.EmailVerificationRequest;
import com.example.Server_electronic_journale.dto.StudentRegistrationRequest;
import com.example.Server_electronic_journale.dto.TeacherSignUpRequest;
import com.example.Server_electronic_journale.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<String> getAuthInfo() {
        return ResponseEntity.ok("Authentication endpoint is active");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());
        try {
            AuthResponse response = authenticationService.signIn(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for email: {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/register/student")
    public ResponseEntity<String> registerStudent(@RequestBody StudentRegistrationRequest request) {
        logger.info("Student registration request received for email: {}", request.getEmail());
        try {
            authenticationService.registerStudent(request);
            return ResponseEntity.ok("На указанный email отправлен код подтверждения");
        } catch (Exception e) {
            logger.error("Student registration failed for email: {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/confirm-registration/student")
    public ResponseEntity<String> confirmStudentRegistration(@RequestBody EmailVerificationRequest request) {
        logger.info("Student email confirmation request received for email: {}", request.getEmail());
        try {
            authenticationService.confirmStudentRegistration(request.getEmail(), request.getCode());
            return ResponseEntity.ok("Регистрация студента завершена");
        } catch (Exception e) {
            logger.error("Student email confirmation failed for email: {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/register/teacher")
    public ResponseEntity<String> registerTeacher(@RequestBody TeacherSignUpRequest request) {
        logger.info("Teacher registration request received for email: {}", request.getEmail());
        try {
            authenticationService.registerTeacher(request);
            return ResponseEntity.ok("На указанный email отправлен код подтверждения");
        } catch (Exception e) {
            logger.error("Teacher registration failed for email: {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/confirm-registration/teacher")
    public ResponseEntity<String> confirmTeacherRegistration(@RequestBody EmailVerificationRequest request) {
        logger.info("Teacher email confirmation request received for email: {}", request.getEmail());
        try {
            authenticationService.confirmTeacherRegistration(request.getEmail(), request.getCode());
            return ResponseEntity.ok("Регистрация учителя завершена");
        } catch (Exception e) {
            logger.error("Teacher email confirmation failed for email: {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

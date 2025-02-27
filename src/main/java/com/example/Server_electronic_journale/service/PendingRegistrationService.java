package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.StudentRegistrationRequest;
import com.example.Server_electronic_journale.dto.TeacherSignUpRequest;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PendingRegistrationService {
    private final Map<String, StudentRegistrationRequest> pendingStudentRegistrations = new ConcurrentHashMap<>();
    private final Map<String, TeacherSignUpRequest> pendingTeacherRegistrations = new ConcurrentHashMap<>();

    public void addPendingStudentRegistration(StudentRegistrationRequest request) {
        pendingStudentRegistrations.put(request.getEmail(), request);
    }

    public StudentRegistrationRequest getPendingStudentRegistration(String email) {
        return pendingStudentRegistrations.get(email);
    }

    public void removePendingStudentRegistration(String email) {
        pendingStudentRegistrations.remove(email);
    }

    public void addPendingTeacherRegistration(TeacherSignUpRequest request) {
        pendingTeacherRegistrations.put(request.getEmail(), request);
    }

    public TeacherSignUpRequest getPendingTeacherRegistration(String email) {
        return pendingTeacherRegistrations.get(email);
    }

    public void removePendingTeacherRegistration(String email) {
        pendingTeacherRegistrations.remove(email);
    }
}

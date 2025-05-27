package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.StudentRegistrationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PendingRegistrationServiceTest {

    private final PendingRegistrationService svc = new PendingRegistrationService();

    @Test
    void addGetRemoveFlow() {
        StudentRegistrationRequest req = new StudentRegistrationRequest();
        req.setName("Ivan");
        req.setSurname("Ivanov");
        req.setPatronymic("Ivanovich");
        req.setDateOfBirth("2000-01-01");
        req.setEmail("x@y.z");
        req.setPassword("pwd");
        req.setGroupId(5);

        svc.addPendingStudentRegistration(req);
        var saved = svc.getPendingStudentRegistration("x@y.z");
        assertNotNull(saved);
        assertEquals("x@y.z", saved.getEmail());

        svc.removePendingStudentRegistration("x@y.z");
        assertNull(svc.getPendingStudentRegistration("x@y.z"));
    }
}

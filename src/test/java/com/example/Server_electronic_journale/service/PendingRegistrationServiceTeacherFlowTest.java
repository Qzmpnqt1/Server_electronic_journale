package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.TeacherSignUpRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PendingRegistrationServiceTeacherFlowTest {

    @Test
    void addGetRemoveTeacherRegistration() {
        PendingRegistrationService svc = new PendingRegistrationService();

        TeacherSignUpRequest req = new TeacherSignUpRequest();
        req.setEmail("teacher@host.com");
        req.setPassword("pwd");
        req.setName("Ivan");
        req.setSurname("Petrov");
        req.setPatronymic("Sergeevich");
        req.setSubjectIds(java.util.Set.of(1,2,3));

        // add
        svc.addPendingTeacherRegistration(req);
        assertNotNull(svc.getPendingTeacherRegistration("teacher@host.com"));

        // get
        TeacherSignUpRequest stored = svc.getPendingTeacherRegistration("teacher@host.com");
        assertEquals("Ivan",   stored.getName());
        assertEquals("Petrov", stored.getSurname());

        // remove
        svc.removePendingTeacherRegistration("teacher@host.com");
        assertNull(svc.getPendingTeacherRegistration("teacher@host.com"));
    }
}

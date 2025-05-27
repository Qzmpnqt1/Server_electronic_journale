package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.AuthRequest;
import com.example.Server_electronic_journale.dto.AuthResponse;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authService;

    @Mock private AdministratorRepository       adminRepo;
    @Mock private StudentRepository             studentRepo;
    @Mock private TeacherRepository             teacherRepo;
    @Mock private GroupRepository               groupRepo;
    @Mock private SubjectRepository             subjectRepo;
    @Mock private JwtService                    jwtService;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder encoder;
    @Mock private AuthenticationManager         authManager;
    @Mock private EmailVerificationService      emailSvc;
    @Mock private PendingRegistrationService    pendingSvc;

    @Test
    void signInReturnsTokenAndRole() {
        Student st = Student.builder()
                .email("student@mail.com")
                .password("enc")
                .role("ROLE_STUDENT")
                .build();

        when(studentRepo.findByEmail("student@mail.com"))
                .thenReturn(Optional.of(st));
        when(jwtService.generateToken(st)).thenReturn("jwt");
        when(authManager.authenticate(any())).thenReturn(null);

        var req = new AuthRequest();
        req.setEmail("student@mail.com");
        req.setPassword("pwd");

        AuthResponse resp = authService.signIn(req);

        assertEquals("jwt", resp.getToken());
        assertEquals("student@mail.com", resp.getEmail());
        assertEquals("ROLE_STUDENT", resp.getRole());
    }

    @Test
    void registerStudentDuplicateEmailThrows() {
        var dto = new com.example.Server_electronic_journale.dto.StudentRegistrationRequest();
        dto.setEmail("dup@mail.com");

        when(studentRepo.findByEmail("dup@mail.com"))
                .thenReturn(Optional.of(Student.builder().build()));

        assertThrows(IllegalArgumentException.class,
                () -> authService.registerStudent(dto));
    }
}

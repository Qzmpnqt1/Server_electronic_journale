package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.AuthRequest;
import com.example.Server_electronic_journale.dto.AuthResponse;
import com.example.Server_electronic_journale.dto.EmailVerificationRequest;
import com.example.Server_electronic_journale.dto.StudentRegistrationRequest;
import com.example.Server_electronic_journale.dto.TeacherSignUpRequest;
import com.example.Server_electronic_journale.model.Administrator;
import com.example.Server_electronic_journale.model.Gradebook;
import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.model.Teacher;
import com.example.Server_electronic_journale.repository.AdministratorRepository;
import com.example.Server_electronic_journale.repository.GroupRepository;
import com.example.Server_electronic_journale.repository.StudentRepository;
import com.example.Server_electronic_journale.repository.SubjectRepository;
import com.example.Server_electronic_journale.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {

    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailVerificationService emailVerificationService;
    @Autowired
    private PendingRegistrationService pendingRegistrationService;

    public void registerStudent(StudentRegistrationRequest request) {
        if (studentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Группа с ID " + request.getGroupId() + " не найдена"));
        try {
            LocalDate.parse(request.getDateOfBirth());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты рождения. Ожидается yyyy-MM-dd");
        }
        pendingRegistrationService.addPendingStudentRegistration(request);
        emailVerificationService.sendVerificationCode(request.getEmail());
    }

    public void confirmStudentRegistration(String email, String code) {
        boolean verified = emailVerificationService.verifyCode(email, code);
        if (!verified) {
            pendingRegistrationService.removePendingStudentRegistration(email);
            throw new IllegalArgumentException("Неверный или просроченный код подтверждения");
        }
        StudentRegistrationRequest request = pendingRegistrationService.getPendingStudentRegistration(email);
        if (request == null) {
            throw new IllegalArgumentException("Нет ожидаемой регистрации для данного email");
        }
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Группа с ID " + request.getGroupId() + " не найдена"));
        LocalDate dateOfBirth = LocalDate.parse(request.getDateOfBirth());
        Student student = Student.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .patronymic(request.getPatronymic())
                .dateOfBirth(dateOfBirth)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .group(group)
                .role("ROLE_STUDENT")
                .build();
        Gradebook gradebook = Gradebook.builder()
                .student(student)
                .gradeEntries(new ArrayList<>())
                .build();
        student.setGradebook(gradebook);
        studentRepository.save(student);
        pendingRegistrationService.removePendingStudentRegistration(email);
    }

    public void registerTeacher(TeacherSignUpRequest request) {
        if (teacherRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }
        pendingRegistrationService.addPendingTeacherRegistration(request);
        emailVerificationService.sendVerificationCode(request.getEmail());
    }

    public void confirmTeacherRegistration(String email, String code) {
        boolean verified = emailVerificationService.verifyCode(email, code);
        if (!verified) {
            pendingRegistrationService.removePendingTeacherRegistration(email);
            throw new IllegalArgumentException("Неверный или просроченный код подтверждения");
        }
        TeacherSignUpRequest request = pendingRegistrationService.getPendingTeacherRegistration(email);
        if (request == null) {
            throw new IllegalArgumentException("Нет ожидаемой регистрации для данного email");
        }
        Set<Subject> subjects = request.getSubjectIds().stream()
                .map(id -> subjectRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Предмет не найден: ID " + id)))
                .collect(Collectors.toSet());
        Teacher teacher = Teacher.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .patronymic(request.getPatronymic())
                .subjects(subjects)
                .role("ROLE_TEACHER")
                .build();
        teacherRepository.save(teacher);
        pendingRegistrationService.removePendingTeacherRegistration(email);
    }

    public AuthResponse signIn(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        UserDetails userDetails = loadUserByEmail(request.getEmail());
        String jwt = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Роль пользователя не найдена"))
                .getAuthority();
        return new AuthResponse(jwt, userDetails.getUsername(), role);
    }

    private UserDetails loadUserByEmail(String email) {
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isPresent()) {
            return studentOpt.get();
        }
        Optional<Teacher> teacherOpt = teacherRepository.findByEmail(email);
        if (teacherOpt.isPresent()) {
            return teacherOpt.get();
        }
        Optional<Administrator> adminOpt = administratorRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            return adminOpt.get();
        }
        throw new IllegalArgumentException("Пользователь не найден");
    }
}

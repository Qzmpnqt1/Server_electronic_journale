package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.*;
import com.example.Server_electronic_journale.model.*;
import com.example.Server_electronic_journale.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Date;
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

    public void registerStudent(StudentRegistrationRequest request) {
        // Логируем ID группы
        System.out.println("Trying to find group with ID: " + request.getGroupId());

        // Проверка на существующий email
        if (studentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }

        // Получение группы по ID
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Группа с ID " + request.getGroupId() + " не найдена"));

        // Преобразование даты рождения
        LocalDate dateOfBirth;
        try {
            dateOfBirth = LocalDate.parse(request.getDateOfBirth());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты рождения. Ожидается yyyy-MM-dd");
        }

        // Создание нового ученика
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

        // Сохранение ученика
        studentRepository.save(student);
    }

    public void registerTeacher(TeacherSignUpRequest request) {
        // Проверка на существующий email
        if (teacherRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется");
        }

        // Получаем предметы, которые будут у учителя
        Set<Subject> subjects = request.getSubjectIds().stream()
                .map(id -> subjectRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Предмет не найден: ID " + id)))
                .collect(Collectors.toSet());

        // Создание нового учителя
        Teacher teacher = Teacher.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .patronymic(request.getPatronymic())
                .subjects(subjects)
                .role("ROLE_TEACHER")
                .build();

        // Сохраняем учителя
        teacherRepository.save(teacher);
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

        // Добавлена проверка администратора
        Optional<Administrator> adminOpt = administratorRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            return adminOpt.get();
        }

        throw new IllegalArgumentException("Пользователь не найден");
    }
}

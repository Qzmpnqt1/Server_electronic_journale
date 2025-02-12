package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Получение текущего аутентифицированного студента
    public Student getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        String email = authentication.getName();
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
    }
}


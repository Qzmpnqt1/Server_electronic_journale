package com.example.Server_electronic_journale.controller;

import com.example.Server_electronic_journale.dto.GradebookDTO;
import com.example.Server_electronic_journale.model.Gradebook;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.service.GradebookService;
import com.example.Server_electronic_journale.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    private final GradebookService gradebookService;

    @Autowired
    public StudentController(StudentService studentService, GradebookService gradebookService) {
        this.studentService = studentService;
        this.gradebookService = gradebookService;
    }

    @GetMapping("/personal-data")
    public Student getPersonalData() {
        Student student = studentService.getCurrentStudent();
        System.out.println("Retrieved student: " + student.getStudentId() + ", " + student.getName());
        return student;
    }

    @GetMapping("/gradebook")
    public ResponseEntity<GradebookDTO> getGradebookForCurrentStudent(Authentication authentication) {
        // Предполагается, что Authentication содержит информацию о текущем пользователе
        String email = authentication.getName();
        GradebookDTO gradebookDTO = gradebookService.getGradebookByStudentEmail(email);
        return ResponseEntity.ok(gradebookDTO);
    }
}

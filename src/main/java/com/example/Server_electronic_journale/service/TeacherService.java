package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.*;
import com.example.Server_electronic_journale.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GradebookRepository gradebookRepository;
    private final GradeEntryRepository gradeEntryRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository,
                          SubjectRepository subjectRepository,
                          GroupRepository groupRepository,
                          StudentRepository studentRepository,
                          GradebookRepository gradebookRepository,
                          GradeEntryRepository gradeEntryRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.gradebookRepository = gradebookRepository;
        this.gradeEntryRepository = gradeEntryRepository;
    }

    // Получение текущего учителя
    public Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        String email = authentication.getName();
        System.out.println("Аутентифицированный пользователь: " + email); // Логирование
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Учитель не найден"));
    }

    // Получение предметов, которые ведет учитель
    public Set<Subject> getSubjectsForTeacher() {
        Teacher teacher = getCurrentTeacher();
        return teacher.getSubjects();
    }

    // Получение студентов в группе
    public Set<Student> getStudentsInGroup(int groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));
        System.out.println("Найдено студентов: " + group.getStudents().size()); // Логирование
        return group.getStudents();
    }
}

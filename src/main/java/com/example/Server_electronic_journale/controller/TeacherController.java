package com.example.Server_electronic_journale.controller;

import com.example.Server_electronic_journale.dto.*;
import com.example.Server_electronic_journale.model.*;
import com.example.Server_electronic_journale.repository.SubjectRepository;
import com.example.Server_electronic_journale.service.GradebookService;
import com.example.Server_electronic_journale.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    private final SubjectRepository subjectRepository;

    private  final GradebookService gradebookService;

    @Autowired
    public TeacherController(TeacherService teacherService, SubjectRepository subjectRepository, GradebookService gradebookService) {
        this.teacherService = teacherService;
        this.subjectRepository = subjectRepository;
        this.gradebookService = gradebookService;
    }

    // Эндпоинт для получения предметов учителя
    @GetMapping("/subjects")
    public Set<Subject> getSubjects() {
        return teacherService.getSubjectsForTeacher();
    }

    // Эндпоинт для получения групп по предмету
    @GetMapping("/subjects/{subjectId}/groups")
    public ResponseEntity<List<GroupResponseDTO>> getGroupsForSubject(@PathVariable int subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));

        // Преобразуем группы в DTO для отправки
        List<GroupResponseDTO> groupDTOs = subject.getGroups().stream()
                .map(group -> {
                    GroupResponseDTO dto = new GroupResponseDTO();
                    dto.setGroupId(group.getGroupId());
                    dto.setName(group.getName());
                    dto.setSubjects(
                            group.getSubjects().stream().map(subjectInGroup -> {
                                SubjectResponseDTO subjectDTO = new SubjectResponseDTO();
                                subjectDTO.setSubjectId(subjectInGroup.getSubjectId());
                                subjectDTO.setName(subjectInGroup.getName());
                                subjectDTO.setCourse(subjectInGroup.getCourse());
                                return subjectDTO;
                            }).collect(Collectors.toList())
                    );
                    return dto;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(groupDTOs);
    }


    // Эндпоинт для получения студентов в группе
    @GetMapping("/groups/{groupId}/students")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsInGroup(@PathVariable int groupId) {
        try {
            Set<Student> students = teacherService.getStudentsInGroup(groupId);
            List<StudentResponseDTO> studentDTOs = students.stream()
                    .map(student -> {
                        StudentResponseDTO dto = new StudentResponseDTO();
                        dto.setStudentId(student.getStudentId());
                        dto.setName(student.getName());
                        dto.setSurname(student.getSurname());
                        dto.setPatronymic(student.getPatronymic());
                        dto.setDateOfBirth(student.getDateOfBirth());
                        dto.setEmail(student.getEmail());
                        dto.setRole(student.getRole());
                        return dto;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(studentDTOs);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/add-grade")
    public GradeEntry addGrade(@RequestBody GradeEntryRequest gradeEntryRequest) {
        return gradebookService.addGrade(
                gradeEntryRequest.getStudentId(),
                gradeEntryRequest.getSubjectId(),
                gradeEntryRequest.getGrade()
        );
    }

    // Эндпоинт для получения личных данных учителя
    @GetMapping("/personal-data")
    public ResponseEntity<TeacherResponseDTO> getPersonalData() {
        try {
            Teacher teacher = teacherService.getCurrentTeacher();
            TeacherResponseDTO dto = new TeacherResponseDTO();
            dto.setTeacherId(teacher.getTeacherId());
            dto.setName(teacher.getName());
            dto.setSurname(teacher.getSurname());
            dto.setPatronymic(teacher.getPatronymic());
            dto.setEmail(teacher.getEmail());
            dto.setRole(teacher.getRole());
            dto.setSubjects(teacher.getSubjects().stream().map(subject -> {
                SubjectResponseDTO subjectDTO = new SubjectResponseDTO();
                subjectDTO.setSubjectId(subject.getSubjectId());
                subjectDTO.setName(subject.getName());
                subjectDTO.setCourse(subject.getCourse());
                return subjectDTO;
            }).collect(Collectors.toSet()));
            // Логирование
            System.out.println("Данные учителя отправлены: " + dto.getEmail());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка при получении данных учителя: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}

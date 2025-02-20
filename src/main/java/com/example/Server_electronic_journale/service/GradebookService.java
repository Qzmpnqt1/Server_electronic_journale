package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.GradeEntryDTO;
import com.example.Server_electronic_journale.dto.GradebookDTO;
import com.example.Server_electronic_journale.model.*;
import com.example.Server_electronic_journale.repository.GradeEntryRepository;
import com.example.Server_electronic_journale.repository.GradebookRepository;
import com.example.Server_electronic_journale.repository.StudentRepository;
import com.example.Server_electronic_journale.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradebookService {

    private final GradebookRepository gradebookRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final TeacherService teacherService;
    private final GradeEntryRepository gradeEntryRepository;

    @Autowired
    public GradebookService(GradebookRepository gradebookRepository, SubjectRepository subjectRepository, StudentRepository studentRepository, TeacherService teacherService, GradeEntryRepository gradeEntryRepository) {
        this.gradebookRepository = gradebookRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.teacherService = teacherService;
        this.gradeEntryRepository = gradeEntryRepository;
    }

    @Transactional
    public GradeEntry addGrade(int studentId, int subjectId, int grade) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));

        Teacher teacher = teacherService.getCurrentTeacher();
        if (!teacher.getSubjects().contains(subject)) {
            throw new IllegalArgumentException("Вы не преподаете этот предмет");
        }

        if (!subject.getGroups().contains(student.getGroup())) {
            throw new IllegalArgumentException("Студент не изучает этот предмет");
        }

        Gradebook gradebook = student.getGradebook();
        if (gradebook == null) {
            gradebook = new Gradebook();
            gradebook.setStudent(student);
            student.setGradebook(gradebook);
            gradebookRepository.save(gradebook);
        }

        // Определяем текущую дату и сессионный период
        LocalDate today = LocalDate.now();
        LocalDateTime sessionStart;
        LocalDateTime sessionEnd;
        if (today.getMonthValue() == 1 && today.getDayOfMonth() >= 9 && today.getDayOfMonth() <= 31) {
            // Зимняя сессия
            sessionStart = LocalDateTime.of(today.getYear(), 1, 9, 0, 0);
            sessionEnd = LocalDateTime.of(today.getYear(), 1, 31, 23, 59, 59);
        } else if (today.getMonthValue() == 6 && today.getDayOfMonth() >= 5 && today.getDayOfMonth() <= 30) {
            // Летняя сессия
            sessionStart = LocalDateTime.of(today.getYear(), 6, 5, 0, 0);
            sessionEnd = LocalDateTime.of(today.getYear(), 6, 30, 23, 59, 59);
        } else {
            throw new IllegalArgumentException("Оценки могут выставляться только в период сессии: зимняя (9-31 января) или летняя (5-30 июня)");
        }

        // Проверяем, существует ли уже оценка для данного предмета в текущей сессии
        List<GradeEntry> existingEntries = gradeEntryRepository
                .findByGradebookAndSubjectAndDateAssignedBetween(gradebook, subject, sessionStart, sessionEnd);
        if (!existingEntries.isEmpty()) {
            throw new IllegalArgumentException("За текущую сессию уже выставлена оценка для данного предмета");
        }

        // Создаем новую оценку, устанавливаем дату выставления и сохраняем
        GradeEntry gradeEntry = new GradeEntry();
        gradeEntry.setGradebook(gradebook);
        gradeEntry.setSubject(subject);
        gradeEntry.setGrade(grade);
        gradeEntry.setDateAssigned(LocalDateTime.now());

        gradeEntryRepository.save(gradeEntry);
        return gradeEntry;
    }


    @Transactional(readOnly = true)
    public GradebookDTO getGradebookByStudentEmail(String email) {
        Gradebook gradebook = gradebookRepository.findByStudent_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("Зачетка не найдена"));

        List<GradeEntryDTO> gradeEntries = gradebook.getGradeEntries().stream().map(entry -> {
            GradeEntryDTO dto = new GradeEntryDTO();
            dto.setEntryId(entry.getEntryId());
            dto.setGrade(entry.getGrade());
            dto.setSubjectName(entry.getSubject().getName());
            return dto;
        }).collect(Collectors.toList());

        GradebookDTO gradebookDTO = new GradebookDTO();
        gradebookDTO.setGradebookId(gradebook.getGradebookId());
        gradebookDTO.setStudentId(gradebook.getStudent().getStudentId());
        gradebookDTO.setGradeEntries(gradeEntries);

        return gradebookDTO;
    }
}




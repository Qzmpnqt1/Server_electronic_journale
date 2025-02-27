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
import java.util.Map;
import java.util.Set;
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

        // Проверяем, что текущий учитель ведёт этот предмет
        Teacher teacher = teacherService.getCurrentTeacher();
        if (!teacher.getSubjects().contains(subject)) {
            throw new IllegalArgumentException("Вы не преподаете этот предмет");
        }

        // Проверяем, что студент состоит в группе, которая изучает этот предмет
        if (!subject.getGroups().contains(student.getGroup())) {
            throw new IllegalArgumentException("Студент не изучает этот предмет");
        }

        // Ищем gradebook студента
        Gradebook gradebook = student.getGradebook();
        if (gradebook == null) {
            gradebook = new Gradebook();
            gradebook.setStudent(student);
            student.setGradebook(gradebook);
            gradebookRepository.save(gradebook);
        }

        // Ищем, есть ли уже запись GradeEntry для этого предмета
        GradeEntry gradeEntry = gradeEntryRepository.findByGradebookAndSubject(gradebook, subject)
                .orElse(null);

        if (gradeEntry == null) {
            gradeEntry = new GradeEntry();
            gradeEntry.setGradebook(gradebook);
            gradeEntry.setSubject(subject);
        }

        LocalDate today = LocalDate.now();

        if (today.getMonthValue() == 1 && today.getDayOfMonth() >= 9 && today.getDayOfMonth() <= 31) {
            // Зимняя сессия
            if (gradeEntry.getWinterGrade() != null) {
                throw new IllegalArgumentException("Зимняя оценка уже выставлена для данного предмета!");
            }
            gradeEntry.setWinterGrade(grade);
            gradeEntry.setWinterDateAssigned(today);
        } else if (today.getMonthValue() == 6 && today.getDayOfMonth() >= 5 && today.getDayOfMonth() <= 30) {
            // Летняя сессия
            if (gradeEntry.getSummerGrade() != null) {
                throw new IllegalArgumentException("Летняя оценка уже выставлена для данного предмета!");
            }
            gradeEntry.setSummerGrade(grade);
            gradeEntry.setSummerDateAssigned(today);
        } else {
            throw new IllegalArgumentException(
                    "Оценки можно выставлять только в период сессии: зимняя (9-31 января) или летняя (5-30 июня)."
            );
        }

        gradeEntryRepository.save(gradeEntry);
        return gradeEntry;
    }


    @Transactional(readOnly = true)
    public GradebookDTO getGradebookByStudentEmail(String email) {
        // 1) Ищем студента по email
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден по email: " + email));

        // 2) У него должна быть группа
        Group group = student.getGroup();
        if (group == null) {
            throw new IllegalArgumentException("У студента нет группы");
        }

        // 3) Получаем зачетку
        Gradebook gradebook = student.getGradebook();
        if (gradebook == null) {
            // Если у студента ещё нет зачетки, создаём новую
            gradebook = new Gradebook();
            gradebook.setStudent(student);
            student.setGradebook(gradebook);
            gradebookRepository.save(gradebook);
        }

        // 4) Стягиваем все предметы, которые изучает эта группа
        Set<Subject> groupSubjects = group.getSubjects();

        // 5) Собираем уже существующие GradeEntry в Map<subjectId, GradeEntry>
        Map<Integer, GradeEntry> subjectToEntry = gradebook.getGradeEntries().stream()
                .collect(Collectors.toMap(e -> e.getSubject().getSubjectId(), e -> e));

        // 6) Формируем список DTO: для каждого предмета создаём запись
        List<GradeEntryDTO> finalList = groupSubjects.stream().map(subj -> {
            // Если у студента уже есть запись для этого предмета
            GradeEntry entry = subjectToEntry.get(subj.getSubjectId());

            GradeEntryDTO dto = new GradeEntryDTO();
            dto.setSubjectName(subj.getName());

            if (entry != null) {
                dto.setEntryId(entry.getEntryId());
                dto.setWinterGrade(entry.getWinterGrade());
                dto.setWinterDateAssigned(entry.getWinterDateAssigned());
                dto.setSummerGrade(entry.getSummerGrade());
                dto.setSummerDateAssigned(entry.getSummerDateAssigned());
            } else {
                // Нет записи -> оценок нет
                dto.setEntryId(0); // 0 или можно не заполнять
                dto.setWinterGrade(null);
                dto.setWinterDateAssigned(null);
                dto.setSummerGrade(null);
                dto.setSummerDateAssigned(null);
            }

            return dto;
        }).collect(Collectors.toList());

        // 7) Формируем финальный GradebookDTO
        GradebookDTO gradebookDTO = new GradebookDTO();
        gradebookDTO.setGradebookId(gradebook.getGradebookId());
        gradebookDTO.setStudentId(student.getStudentId());
        gradebookDTO.setGradeEntries(finalList);

        return gradebookDTO;
    }

}




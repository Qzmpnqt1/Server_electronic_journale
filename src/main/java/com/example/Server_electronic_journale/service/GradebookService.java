package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.GradeEntryDTO;
import com.example.Server_electronic_journale.dto.GradebookDTO;
import com.example.Server_electronic_journale.dto.SubjectStatsDTO;
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
import java.util.*;
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

    @Transactional(readOnly = true)
    public List<SubjectStatsDTO> getGroupStatsForStudentEmail(String email) {
        // 1. Находим студента
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден по email: " + email));

        // 2. Берём его группу
        Group group = student.getGroup();
        if (group == null) {
            throw new IllegalArgumentException("У студента нет группы");
        }

        // 3. Все студенты этой группы
        List<Student> groupStudents = studentRepository.findAllByGroup(group);

        // 4. Предметы, которые изучает группа
        Set<Subject> subjects = group.getSubjects();

        List<SubjectStatsDTO> result = new ArrayList<>();

        for (Subject subj : subjects) {
            // Собираем все GradeEntry (зимняя/летняя оценка) для этого предмета по всем студентам группы
            // Для удобства создадим вспомогательную структуру
            List<GradeInfo> winterGrades = new ArrayList<>();
            List<GradeInfo> summerGrades = new ArrayList<>();

            for (Student st : groupStudents) {
                Gradebook gb = st.getGradebook();
                if (gb == null) continue;  // если вдруг нет зачётки, пропустим

                // Ищем GradeEntry для данного subject
                // Можно пройтись в цикле по gb.getGradeEntries(),
                // или использовать репозиторий GradeEntryRepository.findByGradebookAndSubject(...)
                GradeEntry entry = gb.getGradeEntries().stream()
                        .filter(e -> e.getSubject().getSubjectId() == subj.getSubjectId())
                        .findFirst()
                        .orElse(null);

                if (entry != null) {
                    // Если есть зимняя оценка, добавим в winterGrades
                    if (entry.getWinterGrade() != null) {
                        winterGrades.add(new GradeInfo(
                                st.getName() + " " + st.getSurname(), // ФИО (или как-то иначе)
                                entry.getWinterGrade(),
                                entry.getWinterDateAssigned() // дата выставления
                        ));
                    }
                    // Аналогично для летней
                    if (entry.getSummerGrade() != null) {
                        summerGrades.add(new GradeInfo(
                                st.getName() + " " + st.getSurname(),
                                entry.getSummerGrade(),
                                entry.getSummerDateAssigned()
                        ));
                    }
                }
            }

            // Теперь вычисляем: лучшая/худшая/средняя для зимней
            // Лучшая: max по оценке, при равенстве — min по дате
            // Худшая: min по оценке, при равенстве — min по дате
            // Средняя: среднее по оценкам

            // Лучшая зимняя
            GradeInfo bestWinter = winterGrades.stream()
                    .max(Comparator.comparing(GradeInfo::grade)
                            .thenComparing(GradeInfo::dateAssigned)) // сначала макс оценка, если одинаково — "последний" ???
                    // но нам нужна "самая первая выставленная", значит нужно инвертировать сравнение даты
                    // проще сделать свою логику:
                    .orElse(null);

            // Однако, если при равенстве оценок нужно именно "раньше выставленная" — надо аккуратно
            // Смотрите: Comparator.comparing(GradeInfo::grade) DESC,
            // а потом Comparator.comparing(GradeInfo::dateAssigned) ASC
            // можно сделать так:
            GradeInfo bestWinterCorrect = winterGrades.stream()
                    .sorted((g1, g2) -> {
                        // Сначала по оценке (убывание)
                        int cmp = Integer.compare(g2.grade(), g1.grade());
                        if (cmp != 0) return cmp;
                        // если оценки одинаковые, то по дате (возрастание)
                        return g1.dateAssigned().compareTo(g2.dateAssigned());
                    })
                    .findFirst()
                    .orElse(null);

            // То же самое для worst (min по оценке, при равенстве – earliest date):
            GradeInfo worstWinter = winterGrades.stream()
                    .sorted((g1, g2) -> {
                        // Сначала по оценке (возрастание)
                        int cmp = Integer.compare(g1.grade(), g2.grade());
                        if (cmp != 0) return cmp;
                        // если оценки одинаковые, то по дате (возрастание)
                        return g1.dateAssigned().compareTo(g2.dateAssigned());
                    })
                    .findFirst()
                    .orElse(null);

            // Средняя оценка (double)
            OptionalDouble averageWinter = winterGrades.stream()
                    .mapToInt(GradeInfo::grade)
                    .average();

            // Аналогичные действия для summer
            GradeInfo bestSummer = summerGrades.stream()
                    .sorted((g1, g2) -> {
                        // убывание оценки
                        int cmp = Integer.compare(g2.grade(), g1.grade());
                        if (cmp != 0) return cmp;
                        // при равенстве — самая ранняя дата
                        return g1.dateAssigned().compareTo(g2.dateAssigned());
                    })
                    .findFirst()
                    .orElse(null);

            GradeInfo worstSummer = summerGrades.stream()
                    .sorted((g1, g2) -> {
                        // возрастание оценки
                        int cmp = Integer.compare(g1.grade(), g2.grade());
                        if (cmp != 0) return cmp;
                        return g1.dateAssigned().compareTo(g2.dateAssigned());
                    })
                    .findFirst()
                    .orElse(null);

            OptionalDouble averageSummer = summerGrades.stream()
                    .mapToInt(GradeInfo::grade)
                    .average();

            // Сформируем SubjectStatsDTO
            SubjectStatsDTO dto = new SubjectStatsDTO();
            dto.setSubjectName(subj.getName());

            // Зимняя
            if (bestWinter != null) {
                dto.setBestWinterStudent(bestWinterCorrect.studentName());
                dto.setBestWinterGrade(bestWinterCorrect.grade());
            }
            dto.setAverageWinterGrade(averageWinter.isPresent() ? averageWinter.getAsDouble() : null);
            if (worstWinter != null) {
                dto.setWorstWinterStudent(worstWinter.studentName());
                dto.setWorstWinterGrade(worstWinter.grade());
            }

            // Летняя
            if (bestSummer != null) {
                dto.setBestSummerStudent(bestSummer.studentName());
                dto.setBestSummerGrade(bestSummer.grade());
            }
            dto.setAverageSummerGrade(averageSummer.isPresent() ? averageSummer.getAsDouble() : null);
            if (worstSummer != null) {
                dto.setWorstSummerStudent(worstSummer.studentName());
                dto.setWorstSummerGrade(worstSummer.grade());
            }

            result.add(dto);
        }

        return result;
    }

    // Вспомогательная record/класс для хранения (studentName, grade, dateAssigned)
    private record GradeInfo(String studentName, int grade, LocalDate dateAssigned) {}
}




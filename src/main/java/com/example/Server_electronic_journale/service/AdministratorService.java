package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.GroupDTO;
import com.example.Server_electronic_journale.dto.SubjectDTO;
import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdministratorService {

    private final GroupRepository groupRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final GradebookRepository gradebookRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public AdministratorService(GroupRepository groupRepository,
                                SubjectRepository subjectRepository,
                                StudentRepository studentRepository,
                                GradebookRepository gradebookRepository, TeacherRepository teacherRepository) {
        this.groupRepository = groupRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.gradebookRepository = gradebookRepository;
        this.teacherRepository = teacherRepository;
    }

    public Group addGroup(GroupDTO groupDTO) {
        if (groupRepository.existsByName(groupDTO.getName())) {
            throw new IllegalArgumentException("Группа с таким названием уже существует");
        }

        // Получаем список предметов по ID
        List<Subject> subjects = subjectRepository.findAllById(groupDTO.getSubjectIds());
        if (subjects.isEmpty()) {
            throw new IllegalArgumentException("Не найдены предметы с указанными ID");
        }

        Group group = new Group();
        group.setName(groupDTO.getName());
        group.setSubjects(new HashSet<>(subjects));

        // Сохраняем группу (ассоциации будут сохранены благодаря корректной конфигурации ManyToMany)
        return groupRepository.save(group);
    }


    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public void deleteGroup(int groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));

        // Проверка наличия студентов в группе
        if (!group.getStudents().isEmpty()) {
            throw new IllegalArgumentException("Невозможно удалить группу, так как в ней есть студенты.");
        }

        groupRepository.delete(group);
    }

    public Subject addSubject(SubjectDTO subjectDTO) {
        // Проверяем, существует ли предмет с таким названием
        if (subjectRepository.existsByName(subjectDTO.getName())) {
            throw new IllegalArgumentException("Предмет с таким названием уже существует");
        }

        Subject subject = new Subject();
        subject.setName(subjectDTO.getName());
        subject.setCourse(subjectDTO.getCourse());
        subject.setTeachers(new HashSet<>());
        subject.setGroups(new HashSet<>());

        return subjectRepository.save(subject);
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional
    public void removeStudentFromGroup(int studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        gradebookRepository.deleteByStudent(student);
        studentRepository.delete(student);
    }

    // Получение студентов по ID группы
    public Set<Student> getStudentsInGroup(int groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));
        System.out.println("Найдено студентов: " + group.getStudents().size()); // Логирование
        return group.getStudents();
    }

    public void deleteSubject(int subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
        subjectRepository.delete(subject);
    }
}
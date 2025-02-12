package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.model.Teacher;
import com.example.Server_electronic_journale.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(int id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Предмет с ID " + id + " не найден"));
    }

    public Subject assignTeachersToSubject(int subjectId, List<Teacher> teachers) {
        Subject subject = getSubjectById(subjectId);
        subject.getTeachers().clear();
        subject.getTeachers().addAll(teachers);
        return subjectRepository.save(subject);
    }

    public Subject assignGroupsToSubject(int subjectId, List<Group> groups) {
        Subject subject = getSubjectById(subjectId);
        subject.getGroups().clear();
        subject.getGroups().addAll(groups);
        return subjectRepository.save(subject);
    }

    public void deleteSubject(int id) {
        Subject subject = getSubjectById(id);
        if (!subject.getGroups().isEmpty() || !subject.getTeachers().isEmpty()) {
            throw new IllegalStateException("Предмет не может быть удален, пока связан с группами или преподавателями");
        }
        subjectRepository.delete(subject);
    }
}
package com.example.Server_electronic_journale.repository;

import com.example.Server_electronic_journale.model.GradeEntry;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeEntryRepository extends JpaRepository<GradeEntry, Integer> {
    // Метод для поиска оценки студента по предмету
    GradeEntry findByGradebook_StudentAndSubject(Student student, Subject subject);
}

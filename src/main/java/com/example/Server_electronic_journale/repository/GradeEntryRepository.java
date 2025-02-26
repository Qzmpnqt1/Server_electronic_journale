package com.example.Server_electronic_journale.repository;

import com.example.Server_electronic_journale.model.GradeEntry;
import com.example.Server_electronic_journale.model.Gradebook;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeEntryRepository extends JpaRepository<GradeEntry, Integer> {
    // Метод для поиска оценки по gradebook и subject
    Optional<GradeEntry> findByGradebookAndSubject(Gradebook gradebook, Subject subject);

    // Запрос по зимней дате
    List<GradeEntry> findByGradebookAndSubjectAndWinterDateAssignedBetween(
            Gradebook gradebook,
            Subject subject,
            LocalDateTime start,
            LocalDateTime end
    );

    // Запрос по летней дате
    List<GradeEntry> findByGradebookAndSubjectAndSummerDateAssignedBetween(
            Gradebook gradebook,
            Subject subject,
            LocalDateTime start,
            LocalDateTime end
    );
}


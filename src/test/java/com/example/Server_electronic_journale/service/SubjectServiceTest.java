// src/test/java/com/example/Server_electronic_journale/service/SubjectServiceTest.java
package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.model.Teacher;
import com.example.Server_electronic_journale.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @InjectMocks
    private SubjectService subjSvc;

    @Mock
    private SubjectRepository repo;

    @Test
    void assignTeachersReplaces() {
        int subjectId = 1;  // subject_id=1 есть в базе

        Teacher oldT = Teacher.builder().teacherId(1).build();
        Teacher newT = Teacher.builder().teacherId(2).build();

        // используем изменяемый Set, иначе clear() упадёт
        Set<Teacher> initial = new HashSet<>();
        initial.add(oldT);

        Subject subj = Subject.builder()
                .subjectId(subjectId)
                .teachers(initial)
                .build();

        when(repo.findById(subjectId)).thenReturn(Optional.of(subj));
        when(repo.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));

        Subject updated = subjSvc.assignTeachersToSubject(subjectId, List.of(newT));

        assertEquals(1, updated.getTeachers().size());
        assertTrue(updated.getTeachers().contains(newT));
    }
}

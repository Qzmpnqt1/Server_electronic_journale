package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.dto.GroupDTO;
import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministratorServiceTest {

    @InjectMocks
    private AdministratorService adminSvc;

    @Mock private GroupRepository      groupRepo;
    @Mock private SubjectRepository    subjRepo;

    @Test
    void addGroupSuccess() {
        var dto = new GroupDTO();
        dto.setName("IKBO-11-22");
        dto.setSubjectIds(List.of(1));

        when(groupRepo.existsByName("IKBO-11-22")).thenReturn(false);
        when(subjRepo.findAllById(List.of(1)))
                .thenReturn(List.of(
                        Subject.builder()
                                .subjectId(1)
                                .name("Math")
                                .course(1)
                                .build()
                ));
        when(groupRepo.save(any(Group.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Group g = adminSvc.addGroup(dto);
        assertEquals("IKBO-11-22", g.getName());
        assertEquals(1, g.getSubjects().size());
    }

    @Test
    void deleteGroupWithStudentsThrows() {
        Group g = Group.builder().groupId(10).build();
        g.getStudents().add(
                com.example.Server_electronic_journale.model.Student.builder().build()
        );
        when(groupRepo.findById(10)).thenReturn(Optional.of(g));

        assertThrows(IllegalArgumentException.class,
                () -> adminSvc.deleteGroup(10));
    }
}

package com.example.Server_electronic_journale.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subjects", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "subjectId")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private int subjectId;

    @Setter
    @Column(nullable = false, unique = true) // Уникальность на уровне базы данных
    private String name;

    @Setter
    @Column(nullable = false)
    private int course;

    @Builder.Default
    @Setter
    @ManyToMany(mappedBy = "subjects", fetch = FetchType.LAZY)
    private Set<Teacher> teachers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GradeEntry> gradeEntries = new HashSet<>();

    @Builder.Default
    @Setter
    @ManyToMany(mappedBy = "subjects", fetch = FetchType.LAZY)
    private Set<Group> groups = new HashSet<>();
}

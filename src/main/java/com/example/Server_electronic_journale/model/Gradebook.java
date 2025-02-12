package com.example.Server_electronic_journale.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gradebooks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Gradebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gradebook_id")
    private int gradebookId;

    @JsonBackReference // Отключаем сериализацию в поле Gradebook -> Student
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Добавляем каскадное удаление для связи с GradeEntry
    @OneToMany(mappedBy = "gradebook", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeEntry> gradeEntries;
}






package com.example.Server_electronic_journale.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`groups`")
@ToString(exclude = { "students", "subjects" })  // Исключаем рекурсивные ссылки
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private int groupId;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @JsonManagedReference  // На управляющей стороне связи
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Student> students = new HashSet<>();

    @Builder.Default
    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "subject_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();
}


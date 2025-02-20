package com.example.Server_electronic_journale.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = { "gradebook", "subject" })
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "grade_entries")
public class GradeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private int entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gradebook_id", nullable = false)
    @JsonBackReference
    private Gradebook gradebook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private int grade;

    @Column(name = "date_assigned", nullable = false)
    private LocalDateTime dateAssigned;
}







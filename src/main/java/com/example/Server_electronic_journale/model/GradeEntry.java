package com.example.Server_electronic_journale.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
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

    // Оценки за зимнюю и летнюю сессии
    @Column(name = "winter_grade")
    private Integer winterGrade; // может быть null, если ещё не выставлена

    @Column(name = "winter_date_assigned", columnDefinition = "DATETIME(0)")
    private LocalDateTime winterDateAssigned; // дата, когда зимняя оценка была выставлена

    @Column(name = "summer_grade")
    private Integer summerGrade; // может быть null, если ещё не выставлена

    @Column(name = "summer_date_assigned", columnDefinition = "DATETIME(0)")
    private LocalDateTime summerDateAssigned; // дата, когда летняя оценка была выставлена
}

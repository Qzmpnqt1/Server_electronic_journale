package com.example.Server_electronic_journale.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
@ToString(exclude = {"group", "gradebook"})
public class Student implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private int studentId;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    @JsonBackReference
    private Group group;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String surname;

    @Setter
    @Column(nullable = false)
    private String patronymic;

    @Column(nullable = false, name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private String role;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    @JsonManagedReference
    private Gradebook gradebook;

    // Поле для хранения URL фотографии
    @Setter
    @Column(name = "photo_url")
    private String photoUrl;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}

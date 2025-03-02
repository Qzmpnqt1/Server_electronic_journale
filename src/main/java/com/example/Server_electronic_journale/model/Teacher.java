package com.example.Server_electronic_journale.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teachers")
public class Teacher implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_id")
    private int teacherId;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @Column(nullable = false)
    private String surname;

    @Setter
    @Column(nullable = false)
    private String patronymic;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private String role;

    // Добавлено поле для хранения URL фотографии
    @Setter
    @Column(name = "photo_url")
    private String photoUrl;

    @Builder.Default
    @ManyToMany
    @Setter
    @JoinTable(
            name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();

    // Реализация методов UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
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

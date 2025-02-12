package com.example.Server_electronic_journale.repository;

import com.example.Server_electronic_journale.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findByName(String name);

    boolean existsByName(String name);
}
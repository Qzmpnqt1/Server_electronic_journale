package com.example.Server_electronic_journale.controller;

import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.model.Teacher;
import com.example.Server_electronic_journale.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable int id) {
        try {
            return ResponseEntity.ok(subjectService.getSubjectById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/teachers")
    public ResponseEntity<Subject> assignTeachersToSubject(
            @PathVariable int id,
            @RequestBody List<Teacher> teachers) {
        try {
            return ResponseEntity.ok(subjectService.assignTeachersToSubject(id, teachers));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/groups")
    public ResponseEntity<Subject> assignGroupsToSubject(
            @PathVariable int id,
            @RequestBody List<Group> groups) {
        try {
            return ResponseEntity.ok(subjectService.assignGroupsToSubject(id, groups));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable int id) {
        try {
            subjectService.deleteSubject(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
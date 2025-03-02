package com.example.Server_electronic_journale.controller;

import com.example.Server_electronic_journale.dto.GradebookDTO;
import com.example.Server_electronic_journale.dto.SubjectStatsDTO;
import com.example.Server_electronic_journale.dto.UploadPhotoResponse;
import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.service.GradebookService;
import com.example.Server_electronic_journale.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final GradebookService gradebookService;

    @Autowired
    public StudentController(StudentService studentService, GradebookService gradebookService) {
        this.studentService = studentService;
        this.gradebookService = gradebookService;
    }

    @GetMapping("/personal-data")
    public Student getPersonalData() {
        return studentService.getCurrentStudent();
    }

    @GetMapping("/gradebook")
    public ResponseEntity<GradebookDTO> getGradebookForCurrentStudent(Authentication authentication) {
        String email = authentication.getName();
        GradebookDTO gradebookDTO = gradebookService.getGradebookByStudentEmail(email);
        return ResponseEntity.ok(gradebookDTO);
    }

    @GetMapping("/stats")
    public List<SubjectStatsDTO> getStatsForStudent(Authentication authentication) {
        String email = authentication.getName();
        return gradebookService.getGroupStatsForStudentEmail(email);
    }

    // Эндпоинт для загрузки фото: файл сохраняется, предыдущий файл удаляется, а новый URL возвращается в JSON.
    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> uploadPhoto(@RequestParam("photo") MultipartFile photo) {
        try {
            String photoUrl = studentService.savePhotoAndGetUrl(photo);
            return ResponseEntity.ok(new UploadPhotoResponse(photoUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка загрузки файла");
        }
    }
}

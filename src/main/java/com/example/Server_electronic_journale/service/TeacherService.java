package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.Teacher;
import com.example.Server_electronic_journale.model.Subject;
import com.example.Server_electronic_journale.model.Group;
import com.example.Server_electronic_journale.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final GroupRepository groupRepository;
    private final StudentRepository studentRepository;
    private final GradebookRepository gradebookRepository;
    private final GradeEntryRepository gradeEntryRepository;

    // Папка для сохранения фотографий (должна совпадать с настройками ResourceHandler)
    private final String uploadDir = "D:/uploads/";

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    @Autowired
    public TeacherService(
            TeacherRepository teacherRepository,
            SubjectRepository subjectRepository,
            GroupRepository groupRepository,
            StudentRepository studentRepository,
            GradebookRepository gradebookRepository,
            GradeEntryRepository gradeEntryRepository
    ) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.groupRepository = groupRepository;
        this.studentRepository = studentRepository;
        this.gradebookRepository = gradebookRepository;
        this.gradeEntryRepository = gradeEntryRepository;
    }

    // Получение текущего учителя
    public Teacher getCurrentTeacher() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        String email = authentication.getName();
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Учитель не найден"));
    }

    // Получение предметов, которые ведет учитель
    public Set<Subject> getSubjectsForTeacher() {
        Teacher teacher = getCurrentTeacher();
        return teacher.getSubjects();
    }

    // Получение студентов в группе
    public Set<com.example.Server_electronic_journale.model.Student> getStudentsInGroup(int groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));
        return group.getStudents();
    }

    // Сохранение фото учителя, удаляя старое, если есть
    @Transactional
    public String savePhotoAndGetUrl(MultipartFile photo) throws IOException {
        Teacher teacher = getCurrentTeacher();

        // Удаляем старый файл, если он есть
        if (teacher.getPhotoUrl() != null && !teacher.getPhotoUrl().isEmpty()) {
            String oldUrl = teacher.getPhotoUrl();
            int index = oldUrl.lastIndexOf("/uploads/");
            if (index != -1) {
                String oldFileName = oldUrl.substring(index + "/uploads/".length());
                File oldFile = new File(uploadDir, oldFileName);
                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    if (deleted) {
                        logger.info("Старый файл учителя {} удален: {}", teacher.getTeacherId(), oldFile.getAbsolutePath());
                    } else {
                        logger.warn("Не удалось удалить старый файл {}", oldFile.getAbsolutePath());
                    }
                }
            }
        }

        // Генерируем новое имя файла
        String fileName = "teacher_" + teacher.getTeacherId() + "_" + System.currentTimeMillis() + ".jpg";
        logger.info("Сохранение фото для учителя {}. Имя файла: {}", teacher.getTeacherId(), fileName);

        // Убедимся, что директория существует
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            logger.info("Директория {} не существует. Попытка создания...", uploadDir);
            boolean created = uploadFolder.mkdirs();
            if (!created) {
                logger.error("Не удалось создать директорию для загрузки: {}", uploadDir);
                throw new IOException("Не удалось создать директорию для загрузки файлов");
            }
        }

        // Сохраняем файл
        File uploadFile = new File(uploadFolder, fileName);
        try {
            photo.transferTo(uploadFile);
            logger.info("Файл успешно сохранен: {}", uploadFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении файла учителя {}: {}", teacher.getTeacherId(), uploadFile.getAbsolutePath(), e);
            throw e;
        }

        // Формируем URL (совпадает с настройками ResourceHandler в WebMvcConfig)
        String photoUrl = "http://192.168.0.84:8080/uploads/" + fileName;
        teacher.setPhotoUrl(photoUrl);
        teacherRepository.save(teacher);
        logger.info("URL фотографии учителя {} обновлен: {}", teacher.getTeacherId(), photoUrl);

        return photoUrl;
    }
}

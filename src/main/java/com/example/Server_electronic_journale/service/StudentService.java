package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.model.Student;
import com.example.Server_electronic_journale.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    // Абсолютный путь для сохранения фотографий – измените его под вашу систему (например, "D:/uploads/")
    private final String uploadDir = "D:/uploads/";

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Получаем текущего аутентифицированного студента
    public Student getCurrentStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Пользователь не аутентифицирован");
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        String email = authentication.getName();
        return studentRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Студент с email {} не найден", email);
                    return new IllegalArgumentException("Студент не найден");
                });
    }

    // Сохраняем фото во внешнее хранилище, удаляя старый файл (если есть), и обновляем поле photoUrl
    @Transactional
    public String savePhotoAndGetUrl(MultipartFile photo) throws IOException {
        Student student = getCurrentStudent();

        // Если у студента уже есть фото, удаляем старый файл
        if (student.getPhotoUrl() != null && !student.getPhotoUrl().isEmpty()) {
            String oldUrl = student.getPhotoUrl();
            int index = oldUrl.lastIndexOf("/uploads/");
            if (index != -1) {
                String oldFileName = oldUrl.substring(index + "/uploads/".length());
                File oldFile = new File(uploadDir, oldFileName);
                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    if (deleted) {
                        logger.info("Старый файл {} успешно удален", oldFile.getAbsolutePath());
                    } else {
                        logger.warn("Не удалось удалить старый файл {}", oldFile.getAbsolutePath());
                    }
                }
            }
        }

        String fileName = "student_" + student.getStudentId() + "_" + System.currentTimeMillis() + ".jpg";
        logger.info("Сохранение фото для студента {}. Имя файла: {}", student.getStudentId(), fileName);

        // Проверяем, существует ли папка для загрузки и создаём её при необходимости
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            logger.info("Директория {} не существует. Попытка создания...", uploadDir);
            boolean created = uploadFolder.mkdirs();
            if (!created) {
                logger.error("Не удалось создать директорию для загрузки: {}", uploadDir);
                throw new IOException("Не удалось создать директорию для загрузки файлов");
            }
            logger.info("Директория {} успешно создана.", uploadDir);
        }

        // Создаем файл внутри папки
        File uploadFile = new File(uploadFolder, fileName);
        try {
            photo.transferTo(uploadFile);
            logger.info("Файл успешно сохранен: {}", uploadFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Ошибка при сохранении файла: {}", uploadFile.getAbsolutePath(), e);
            throw e;
        }

        // Формируем URL для доступа к файлу (убедитесь, что он соответствует настройкам ResourceHandler)
        String photoUrl = "http://192.168.0.84:8080/uploads/" + fileName;
        student.setPhotoUrl(photoUrl);
        studentRepository.save(student);
        logger.info("URL фотографии студента {} обновлен: {}", student.getStudentId(), photoUrl);
        return photoUrl;
    }
}

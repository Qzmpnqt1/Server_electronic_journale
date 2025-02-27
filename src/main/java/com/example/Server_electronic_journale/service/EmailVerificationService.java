package com.example.Server_electronic_journale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    // Храним не просто код, а объект с кодом и временем генерации
    private final Map<String, CodeAndTime> verificationCodes = new ConcurrentHashMap<>();

    // Время жизни кода в минутах
    private static final long CODE_EXPIRATION_MINUTES = 2;

    /**
     * Генерирует 6-значный код, сохраняет его с текущим временем и отправляет на email.
     */
    public void sendVerificationCode(String email) {
        String code = generateSixDigitCode();
        verificationCodes.put(email, new CodeAndTime(code, LocalDateTime.now()));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Код подтверждения для регистрации");
        message.setText("Ваш код подтверждения: " + code);
        try {
            mailSender.send(message);
            logger.info("Verification email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send verification email to: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Ошибка отправки email. Проверьте настройки SMTP.");
        }
    }

    private String generateSixDigitCode() {
        int code = (int) (100000 + Math.random() * 900000);
        return String.valueOf(code);
    }

    /**
     * Проверяет, совпадает ли введённый код и не просрочен ли он (2 минуты).
     * @return true, если код корректный и не просрочен, иначе false.
     */
    public boolean verifyCode(String email, String submittedCode) {
        CodeAndTime cat = verificationCodes.get(email);
        if (cat == null) {
            logger.warn("No verification code found for email: {}", email);
            return false;
        }
        long minutesPassed = Duration.between(cat.createdAt, LocalDateTime.now()).toMinutes();
        if (minutesPassed >= CODE_EXPIRATION_MINUTES) {
            logger.warn("Verification code for email {} expired ({} minutes passed)", email, minutesPassed);
            verificationCodes.remove(email);
            return false;
        }
        if (cat.code.equals(submittedCode)) {
            logger.info("Verification code for email {} matched successfully", email);
            verificationCodes.remove(email);
            return true;
        }
        logger.warn("Verification code for email {} did not match", email);
        return false;
    }

    /**
     * Вспомогательный класс для хранения кода и времени его генерации.
     */
    private static class CodeAndTime {
        final String code;
        final LocalDateTime createdAt;

        public CodeAndTime(String code, LocalDateTime createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }
    }
}

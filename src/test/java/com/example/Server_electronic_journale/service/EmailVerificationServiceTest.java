package com.example.Server_electronic_journale.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailVerificationService service;

    @Test
    void sendAndVerifyFlow() {
        String email = "user@test.com";

        // перехватываем сообщение, чтобы достать сгенерированный код
        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        doNothing().when(mailSender).send(captor.capture());

        // отправляем код
        service.sendVerificationCode(email);
        verify(mailSender).send(any(SimpleMailMessage.class));

        // Получаем тело письма: "Ваш код подтверждения: 123456"
        String body = captor.getValue().getText();
        String code = body.replaceAll("\\D+", "");   // оставляем только цифры

        // корректный код — true
        assertTrue(service.verifyCode(email, code));

        // Повторная проверка тем же кодом уже должна вернуть false
        assertFalse(service.verifyCode(email, code));

        // Неверный код — всегда false
        assertFalse(service.verifyCode(email, "000000"));
    }
}

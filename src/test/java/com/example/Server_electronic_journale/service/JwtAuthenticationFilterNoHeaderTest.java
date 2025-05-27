package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.config.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterNoHeaderTest {

    @Mock private JwtService              jwtSvc;
    @Mock private UserDetailsServiceImpl  uds;
    @Mock private HttpServletRequest      req;
    @Mock private HttpServletResponse     res;
    @Mock private FilterChain             chain;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthorizationHeader_skipsAuthentication() throws Exception {
        // 1) Заголовка нет
        when(req.getHeader("Authorization")).thenReturn(null);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        // вручную внедряем зависимости
        ReflectionTestUtils.setField(filter, "jwtService", jwtSvc);
        ReflectionTestUtils.setField(filter, "userDetailsService", uds);

        // 2) Запускаем
        filter.doFilter(req, res, chain);

        // 3) Проверяем:
        verify(chain).doFilter(req, res);                // продолжили цепочку
        verifyNoInteractions(jwtSvc, uds);               // сервисы даже не дернули
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

package com.example.Server_electronic_journale.service;

import com.example.Server_electronic_journale.repository.AdministratorRepository;
import com.example.Server_electronic_journale.repository.StudentRepository;
import com.example.Server_electronic_journale.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Проверка на студента
        if (studentRepository.findByEmail(email).isPresent()) {
            return studentRepository.findByEmail(email).get();
        }

        // Проверка на учителя
        if (teacherRepository.findByEmail(email).isPresent()) {
            return teacherRepository.findByEmail(email).get();
        }

        // Проверка на администратора
        if (administratorRepository.findByEmail(email).isPresent()) {
            return administratorRepository.findByEmail(email).get();
        }

        throw new UsernameNotFoundException("Пользователь не найден: " + email);
    }
}
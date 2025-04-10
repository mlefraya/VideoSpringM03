package com.example.VideoSpringM03.config;

import com.example.VideoSpringM03.model.User;
import com.example.VideoSpringM03.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            repository.save(new User("John Doe", "john@example.com"));
            repository.save(new User("Jane Smith", "jane@example.com"));
        };
    }
}
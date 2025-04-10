package com.example.VideoSpringM03.repository;

import com.example.VideoSpringM03.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
package com.queoil.Queoil.repository;

import com.queoil.Queoil.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(String role);
    List<User> findByRoleAndUsernameContainingIgnoreCase(String role, String username);
    Optional<User> findByEmailAndPassword(String email, String password);
}
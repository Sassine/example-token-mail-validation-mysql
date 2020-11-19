package dev.sassine.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sassine.token.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByEmail(String email);
}

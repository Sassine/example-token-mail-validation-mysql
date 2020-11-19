package dev.sassine.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.sassine.token.model.VerificationToken;

import java.util.List;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    List<VerificationToken> findByUserEmail(String email);
    List<VerificationToken> findByToken(String token);
}

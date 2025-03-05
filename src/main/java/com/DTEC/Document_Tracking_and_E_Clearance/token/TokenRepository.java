package com.DTEC.Document_Tracking_and_E_Clearance.token;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByAccessToken(String accessToken);

    @Query("SELECT t FROM Token t WHERE t.refreshToken = :refreshToken AND t.isRevoked = false")
    Optional<Token> findTokenByRefreshToken(String refreshToken);

    @Query("SELECT t FROM Token t WHERE t.refreshToken = ?1")
    Optional<Token> findByRefreshToken(String refreshToken);


    @Query("SELECT t FROM Token t WHERE t.user.username = :username")
    List<Token> findAllValidTokenByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now AND t.isRevoked = true")
    void deleteAllExpiredTokens(@Param("now") Date now);
}

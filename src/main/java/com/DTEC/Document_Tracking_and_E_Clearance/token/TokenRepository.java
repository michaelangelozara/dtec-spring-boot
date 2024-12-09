package com.DTEC.Document_Tracking_and_E_Clearance.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findTokenByRefreshToken(String refreshToken);

    @Query("SELECT t FROM Token t WHERE t.user.username = :username")
    List<Token> findAllValidTokenByUsername(@Param("username") String username);
}

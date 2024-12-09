package com.DTEC.Document_Tracking_and_E_Clearance.configuration;

import com.DTEC.Document_Tracking_and_E_Clearance.token.TokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Value("${header}")
    private String HEADER;

    public LogoutService(
            TokenRepository tokenRepository
    ) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {

        var authHeader = request.getHeader(HEADER);

        if(authHeader == null || !authHeader.startsWith("Bearer "))
            return;

        var refreshToken = extractRefreshToken(request);

        if(refreshToken ==  null)
            return;

        var accessToken = authHeader.substring(7);

        var storedToken = this.tokenRepository.findTokenByRefreshToken(refreshToken)
                .orElse(null);

        if(storedToken != null){
            storedToken.setAccessToken(accessToken);
            storedToken.setRevoked(true);
            this.tokenRepository.save(storedToken);
            SecurityContextHolder.clearContext();

            removeRefreshTokenFromHttpOnly(response);
        }

    }

    private String extractRefreshToken(
            HttpServletRequest request
    ) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private void removeRefreshTokenFromHttpOnly(
            HttpServletResponse response
    ){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}

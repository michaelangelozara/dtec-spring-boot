package com.DTEC.Document_Tracking_and_E_Clearance.authentication;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.configuration.JwtService;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.token.TokenRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.LoginRequestDto;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public AuthenticationController(UserService userService, JwtService jwtService, UserRepository userRepository, TokenRepository tokenRepository, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<String>> authenticate(
            @Validated @RequestBody LoginRequestDto dto,
            HttpServletResponse response
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "User Successfully Logged in",
                        this.userService.authenticate(dto, response),
                        "",
                        this.dateTimeFormatterUtil.getDateTime()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            HttpServletRequest request
    ) {
        try {
            final String refreshToken = extractRefreshToken(request);

            if (refreshToken == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Refresh token is missing", null, "", this.dateTimeFormatterUtil.getDateTime()));

            final String username = jwtService.extractUsername(refreshToken);
            if (username != null) {

                var user = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UnauthorizedException("Invalid user, please re-log in to your account"));

                var isValidToken = this.tokenRepository.findTokenByRefreshToken(refreshToken)
                        .map(t -> !t.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(refreshToken, user) && isValidToken) {
                    // Generate a new access token
                    String accessToken = jwtService.generateToken(user);

                    // Send the access token back to the client
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "", "Bearer " + accessToken, "", this.dateTimeFormatterUtil.getDateTime()));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Invalid Refresh Token", null, "", this.dateTimeFormatterUtil.getDateTime()));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Invalid Refresh Token", null, "", this.dateTimeFormatterUtil.getDateTime()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Invalid Refresh Token", null, "", this.dateTimeFormatterUtil.getDateTime()));
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
}

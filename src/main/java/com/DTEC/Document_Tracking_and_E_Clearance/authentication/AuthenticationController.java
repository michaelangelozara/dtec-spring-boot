package com.DTEC.Document_Tracking_and_E_Clearance.authentication;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.configuration.JwtService;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.FingerprintService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final FingerprintService fingerprintService;

    public AuthenticationController(UserService userService, JwtService jwtService, UserRepository userRepository, TokenRepository tokenRepository, DateTimeFormatterUtil dateTimeFormatterUtil, FingerprintService fingerprintService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.fingerprintService = fingerprintService;
    }

    @PostMapping("/fingerprints")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getAllStoredFingerprints(
            @RequestBody Map<String, String> map
    ) {
        String code = map.get("code");
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "",
                                this.fingerprintService.getAllFingerprints(code),
                                "",
                                null
                        )
                );
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
                        this.userService.authenticate(dto, response, true),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()));
    }

    @PostMapping("/fingerprint/authenticate")
    public ResponseEntity<ApiResponse<String>> authenticateFingerPrint(
            @Validated @RequestBody LoginRequestDto dto,
            HttpServletResponse response
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(
                        true,
                        "User Successfully Logged in",
                        this.userService.authenticate(dto, response, false),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(
            HttpServletRequest request
    ) {
        try {
            final String refreshToken = extractRefreshToken(request);

            if (refreshToken == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Refresh token is missing", null, "", this.dateTimeFormatterUtil.formatIntoDateTime()));

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
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "", "Bearer " + accessToken, "", this.dateTimeFormatterUtil.formatIntoDateTime()));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Session Expired", null, "", this.dateTimeFormatterUtil.formatIntoDateTime()));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Session Expired", null, "", this.dateTimeFormatterUtil.formatIntoDateTime()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, "Session Expired", null, "", this.dateTimeFormatterUtil.formatIntoDateTime()));
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

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody Map<String, String> map
    ) {
        String password1 = map.get("password1");
        String password2 = map.get("password2");
        String token = map.get("token");
        this.userService.changePassword(password1, password2, token);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "Password is Successfully Updated",
                                null,
                                "",
                                this.dateTimeFormatterUtil.formatIntoDateTime()
                        )
                );
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestParam("e") String email
    ) {
        this.userService.forgotPassword(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "Reset Link has been sent to you Registered Email Address",
                                null,
                                "",
                                ""
                        )
                );
    }
}

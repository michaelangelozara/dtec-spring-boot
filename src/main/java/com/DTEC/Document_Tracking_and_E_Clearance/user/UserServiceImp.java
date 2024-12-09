package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.configuration.JwtService;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.*;
import com.DTEC.Document_Tracking_and_E_Clearance.token.Token;
import com.DTEC.Document_Tracking_and_E_Clearance.token.TokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CourseRepository courseRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserUtil userUtil;

    @Value("${application.security.jwt.cookie-expiration}")
    private long COOKIE_EXPIRATION;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper, CourseRepository courseRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, UserUtil userUtil) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.courseRepository = courseRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.userUtil = userUtil;
    }

    @Override
    public UserInfoResponseDto me() {
        var user = this.userUtil.getCurrentUser();
        if (user == null)
            throw new UnauthorizedException("Invalid User Credentials");

        return this.userMapper.toUserInfoResponseDto(user);
    }

    @Override
    public String deleteUser(int id) {
        var user = this.userRepository.findById(id).orElse(null);
        if(user == null)
            throw new ResourceNotFoundException("Deletion Failed. Invalid User");

        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now());
        this.userRepository.save(user);
        return "User Successfully Deleted";
    }

    @Override
    public void createUser(UserRegisterRequestDto dto) {
        if (!UserRegex.validateStudentUsername(dto.username()))
            throw new BadRequestException("User ID is Invalid Format");

        // check if the student is existing already
        if (this.userRepository.existsByUsername(dto.username()))
            throw new ConflictException("User ID is existing already");

        var student = this.userMapper.toUser(dto);
        this.userRepository.save(student);
    }

    @Override
    public UserInfoResponseDto getUserById(int id) {
        var user = this.userRepository.findById(id)
                .orElse(null);

        if (user == null)
            throw new ResourceNotFoundException("User Not Found");

        return this.userMapper.toUserInfoResponseDto(user);
    }

    @Override
    public List<UserInfoResponseDto> getAllUsers(int startFrom, int endTo) {
        Pageable pageable = PageRequest.of(startFrom, endTo);
        Page<User> users = this.userRepository.findAll(pageable);
        if (users.isEmpty())
            throw new NoContentException("No Registered Student yet");

        return this.userMapper.toUserInfoDtoList(
                users.getContent()
                        .stream()
                        .filter(u -> !u.getRole().equals(Role.SUPER_ADMIN) && !u.isDeleted())
                        .toList()
        );
    }

    @Override
    public String authenticate(LoginRequestDto dto, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.username(),
                            dto.password()
                    )
            );

            // get the user by its username
            var user = this.userRepository.findByUsername(dto.username())
                    .orElseThrow();

            // generate new access token that will be passed to the user
            var accessToken = this.jwtService.generateToken(user);

            // this is the refresher token, in case the access token expires
            // this can use to request a new access token
            var refreshToken = this.jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);

            saveUserTokens(user, refreshToken);

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)        // Ensure it's sent only over HTTPS
                    .path("/")           // Accessible throughout your domain
                    .maxAge(COOKIE_EXPIRATION)  // Example: 7 days expiry
                    .sameSite("None")  // Prevents CSRF attacks
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return "Bearer " + accessToken;
        } catch (BadCredentialsException e) {
            if (dto.username().isEmpty() || dto.password().isEmpty())
                throw new UnauthorizedException("User ID  or Password cannot be Empty");
            throw new UnauthorizedException("Invalid User ID or Password");
        } catch (InternalAuthenticationServiceException e) {
            throw new UnauthorizedException("Invalid User ID or Password");
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private void saveUserTokens(
            User user,
            String refreshToken
    ) {
        var token = new Token();
        token.setRefreshToken(refreshToken);
        token.setRevoked(false);
        token.setExpiresAt(this.jwtService.extractExpiration(refreshToken));
        token.setUser(user);
        this.tokenRepository.save(token);
    }

    private void revokeAllUserTokens(
            User user
    ) {
        var validUserTokens = this.tokenRepository.findAllValidTokenByUsername(user.getUsername());
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(t -> {
            t.setRevoked(true);
        });

        this.tokenRepository.saveAll(validUserTokens);
    }
}

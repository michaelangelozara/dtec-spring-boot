package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.DTEC.Document_Tracking_and_E_Clearance.club.Type;
import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRole;
import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.configuration.JwtService;
import com.DTEC.Document_Tracking_and_E_Clearance.course.Course;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.department.Department;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.*;
import com.DTEC.Document_Tracking_and_E_Clearance.token.Token;
import com.DTEC.Document_Tracking_and_E_Clearance.token.TokenRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final MemberRoleRepository memberRoleRepository;

    @Value("${application.security.jwt.cookie-expiration}")
    private long COOKIE_EXPIRATION;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper, CourseRepository courseRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, UserUtil userUtil, ClubRepository clubRepository, DepartmentRepository departmentRepository, MemberRoleRepository memberRoleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.courseRepository = courseRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.userUtil = userUtil;
        this.clubRepository = clubRepository;
        this.departmentRepository = departmentRepository;
        this.memberRoleRepository = memberRoleRepository;
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
        if (user == null)
            throw new ResourceNotFoundException("Deletion Failed. Invalid User");

        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now());
        this.userRepository.save(user);
        return "User Successfully Deleted";
    }

    @Override
    public String resetPassword(int userId) {
        var user = this.userRepository
                .findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        final String defaultPassword = "1234";
        user.setPassword(this.passwordEncoder.encode(defaultPassword));
        user.setFirstTimeLogin(true);
        this.userRepository.save(user);

        return user.getLastname() + ", " + user.getFirstName()+"\'s Password Successfully Reset";
    }

    @Transactional
    @Override
    public void createUser(UserRegisterRequestDto dto) {
        if (!UserRegex.validateStudentUsername(dto.username()))
            throw new ForbiddenException("User ID is Invalid Format");

        // check if the user is existing already
        if (this.userRepository.existsByUsername(dto.username()))
            throw new ConflictException("User ID is existing already");

        // check if the email is existing already
        if (this.userRepository.existsByEmail(dto.email()))
            throw new ConflictException("Email is existing already");

        var user = this.userMapper.toUser(dto);

        var savedUser = this.userRepository.save(user);

        if(isRoleIncluded(dto.role())) return;

        if (dto.role().equals(Role.MODERATOR)) {
            var club = this.clubRepository.findById(dto.moderatorClubId())
                    .orElseThrow(() -> new ResourceNotFoundException("Club not Found"));

            var memberRole = MemberRole.builder()
                    .role(ClubRole.MODERATOR)
                    .user(savedUser)
                    .club(club)
                    .build();

            this.memberRoleRepository.save(memberRole);
        } else if (dto.role().equals(Role.STUDENT_OFFICER)) {
            if (dto.yearLevel() == 0) throw new ForbiddenException("Please Select Student Year Level");

            if(dto.departmentClubRole().equals(ClubRole.STUDENT_OFFICER) && dto.socialClubRole().equals(ClubRole.STUDENT_OFFICER))
                throw new ForbiddenException("Multiple \"Student officer\" role in multiple departmentClub is Prohibited");

            if((dto.departmentClubRole().equals(ClubRole.MEMBER) && dto.socialClubRole().equals(ClubRole.MEMBER)))
                throw new ForbiddenException("The Student Officer must be Officer to either Department or Social Club");

            user.setYearLevel(dto.yearLevel());
            user.setDepartment(getDepartment(dto.departmentId()));
            user.setCourse(getCourse(dto.courseId()));

            var departmentClub = getClub(dto.departmentClubId(), Type.DEPARTMENT);
            var socialClub = getClub(dto.socialClubId(), Type.SOCIAL);

            var departmentMemberRole = MemberRole.builder()
                    .role(dto.departmentClubRole())
                    .user(savedUser)
                    .club(departmentClub)
                    .build();

            var socialMemberRole = MemberRole.builder()
                    .role(dto.socialClubRole())
                    .user(savedUser)
                    .club(socialClub)
                    .build();
            this.memberRoleRepository.saveAll(List.of(departmentMemberRole, socialMemberRole));
        }else if(dto.role().equals(Role.STUDENT) ){
            if (dto.yearLevel() == 0) throw new ForbiddenException("Please Select Student Year Level");

            user.setYearLevel(dto.yearLevel());
            user.setDepartment(getDepartment(dto.departmentId()));
            user.setCourse(getCourse(dto.courseId()));

            var departmentClub = getClub(dto.departmentClubId(), Type.DEPARTMENT);
            var socialClub = getClub(dto.socialClubId(), Type.SOCIAL);

            var departmentMemberRole = MemberRole.builder()
                    .role(ClubRole.MEMBER)
                    .user(savedUser)
                    .club(departmentClub)
                    .build();

            var socialMemberRole = MemberRole.builder()
                    .role(ClubRole.MEMBER)
                    .user(savedUser)
                    .club(socialClub)
                    .build();
            this.memberRoleRepository.saveAll(List.of(departmentMemberRole, socialMemberRole));
        } else {
            throw new ForbiddenException("Please Select a Valid Role");
        }
    }

    // these roles don't need department, course, departmentClub and etc
    private boolean isRoleIncluded(Role role){
        Set<Role> roles = new HashSet<>();
        roles.add(Role.SUPER_ADMIN);
        roles.add(Role.ADMIN);
        roles.add(Role.OFFICE_IN_CHARGE);
        roles.add(Role.DSA);
        roles.add(Role.PRESIDENT);
        roles.add(Role.COMMUNITY);
        roles.add(Role.FINANCE);

        return roles.contains(role);
    }

    public Club getClub(int id, Type type){
        return this.clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(type.equals(Type.DEPARTMENT) ? "Department Club not Found" : "Social Club not Found"));
    }

    private Department getDepartment(int id) {
        return this.departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not Found"));
    }

    private Course getCourse(int id) {
        return this.courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not Found"));
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
            throw new ResourceNotFoundException("No Registered Student yet");

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

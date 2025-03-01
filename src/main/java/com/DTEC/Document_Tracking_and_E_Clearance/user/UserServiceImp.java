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
import com.DTEC.Document_Tracking_and_E_Clearance.email.EmailService;
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
    private final EmailService emailService;

    @Value("${application.security.jwt.cookie-expiration}")
    private long COOKIE_EXPIRATION;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper, CourseRepository courseRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenRepository tokenRepository, UserUtil userUtil, ClubRepository clubRepository, DepartmentRepository departmentRepository, MemberRoleRepository memberRoleRepository, EmailService emailService) {
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
        this.emailService = emailService;
    }

    @Override
    public UserInfoResponseDto me() {
        var user = this.userUtil.getCurrentUser();
        if (user == null)
            throw new UnauthorizedException("Invalid User Credentials");

        return this.userMapper.toUserInfoResponseDto(user);
    }

    @Override
    public void deleteUser(int id) {
        var user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        user.setDeleted(true);
        user.setDeletedAt(LocalDate.now());
        this.userRepository.save(user);
    }

    @Override
    public String resetPassword(int userId) {
        var user = this.userRepository
                .findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        final String defaultPassword = "1234";
        user.setPassword(this.passwordEncoder.encode(defaultPassword));
        user.setFirstTimeLogin(true);
        var updatedUser = this.userRepository.save(user);

        // send email
        String token = this.jwtService.generateRefreshToken(updatedUser);
        this.emailService.sendEmail(UserUtil.removeWhiteSpace(updatedUser.getEmail()), token);

        return user.getLastname() + ", " + user.getFirstName() + "\'s Password Successfully Reset";
    }

    @Transactional
    @Override
    public void update(UserRegisterRequestDto dto, int userId) {
        if (dto.role().equals(Role.SUPER_ADMIN))
            throw new ForbiddenException("Invalid Role");

        // check if the email is existing already
        if (this.userRepository.existsByContactNumber(dto.contactNumber()))
            throw new ConflictException("Contact Number is existing already");

        // validate contact number
        if (!UserUtil.validateContactNumber(dto.contactNumber()))
            throw new ForbiddenException("Invalid Contact Number");

        // validate gmail
        if (!UserUtil.validateGmail(dto.email()))
            throw new ForbiddenException("Invalid Gmail");

        var user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        // check if the email is existing already
        if (!user.getEmail().equals(dto.email()) && this.userRepository.existsByEmail(dto.email()))
            throw new ConflictException("Email is existing already");

        user.setFirstName(dto.firstName());
        user.setMiddleName(dto.middleName());
        user.setLastname(dto.lastname());
        user.setEmail(UserUtil.removeWhiteSpace(dto.email()));
        user.setRole(dto.role());
        user.setContactNumber(UserUtil.extractContactNumber(dto.contactNumber()));

        var savedUser = this.userRepository.save(user);

        if (isRoleIncluded(dto.role())) return;

        setUpUser(savedUser, dto);
    }

    @Override
    public DetailedUserResponseDto getUserById(int id) {
        var user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));
        return this.userMapper.toDetailedUserResponseDto(user);
    }

    @Override
    public List<UserInfoResponseDto> searchUsers(String searchTerm) {
        var users = this.userRepository.findUserBySearchTerm(searchTerm);
        return this.userMapper.toUserInfoDtoList(users);
    }

    @Transactional
    @Override
    public void changePassword(String password1, String password2, String token) {
        var username = this.jwtService.extractUsername(token);
        var fetchedUser = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        if (token == null || token.isEmpty()) throw new UnauthorizedException("Invalid Token");

        // check if the token is still valid
        if (!this.jwtService.isTokenValid(token, fetchedUser))
            throw new UnauthorizedException("Account Access Expired! Please Visit the ICTSO to Reset Access");

        // check if the passwords match
        if (password1.equals(password2)) {
            if (password1.isEmpty())
                throw new ForbiddenException("Password is Empty");

            if (password1.length() < 8)
                throw new ForbiddenException("Please Provide Password more than 7 Characters");

            fetchedUser.setFirstTimeLogin(false);
            fetchedUser.setPassword(this.passwordEncoder.encode(password1));
            this.userRepository.save(fetchedUser);
        } else {
            throw new ForbiddenException("Passwords do not match!");
        }
    }

    @Override
    public String getModeratorStudentOfficerESignature() {
        var user = this.userUtil.getCurrentUser();
        var fetchedUser = this.userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        if (fetchedUser.getESignature() == null || fetchedUser.getESignature().isEmpty())
            throw new ResourceNotFoundException("Please Contact the Admin to Register your E-Signature");

        return fetchedUser.getESignature();
    }

    private void setUpUser(User savedUser, UserRegisterRequestDto dto) {
        if (dto.role().equals(Role.PROGRAM_HEAD)) {
            var course = getCourse(dto.courseId());
            var department = getDepartment(dto.departmentId());

            var oldProgramHeadOptional = course.getUsers().stream().filter(u -> u.getRole().equals(Role.PROGRAM_HEAD)).findFirst();

            // change the role of the old program head
            if (oldProgramHeadOptional.isPresent()) {
                var oldProgramHead = oldProgramHeadOptional.get();
                oldProgramHead.setRole(Role.PERSONNEL);
                this.userRepository.save(oldProgramHead);
            }

            savedUser.setCourse(course);
            savedUser.setDepartment(department);
            this.userRepository.save(savedUser);
        } else if (dto.role().equals(Role.DEAN)) {
            var department = getDepartment(dto.departmentId());

            var oldDean = department.getUsers().stream().filter(u -> u.getRole().equals(Role.DEAN)).findFirst();

            // change the role of the old program head
            if (oldDean.isPresent()) {
                var oldProgramHead = oldDean.get();
                oldProgramHead.setRole(Role.PERSONNEL);
                this.userRepository.save(oldProgramHead);
            }

            savedUser.setDepartment(department);
            this.userRepository.save(savedUser);
        } else if (dto.role().equals(Role.MODERATOR) || dto.role().equals(Role.PERSONNEL)) {
            if (dto.role().equals(Role.MODERATOR)) {
                var club = this.clubRepository.findById(dto.moderatorClubId())
                        .orElseThrow(() -> new ResourceNotFoundException("Club not Found"));

                // remove the old moderator from the club
                var memberRoles = this.memberRoleRepository.findMemberRoleByClubId(dto.moderatorClubId(), Role.MODERATOR);
                unregisterPersonnelForBeingModerator(memberRoles);

                var memberRole = MemberRole.builder()
                        .role(ClubRole.MODERATOR)
                        .user(savedUser)
                        .club(club)
                        .build();

                this.memberRoleRepository.save(memberRole);

                // set moderator to acad type
                savedUser.setType(PersonnelType.ACADEMIC);
                var department = getDepartment(dto.departmentId());
                var course = getCourse(dto.courseId());
                savedUser.setDepartment(department);
                savedUser.setCourse(course);
            } else {
                if (dto.type().equals(PersonnelType.ACADEMIC)) {
                    var department = getDepartment(dto.departmentId());
                    var course = getCourse(dto.courseId());
                    savedUser.setDepartment(department);
                    savedUser.setCourse(course);
                } else {
                    savedUser.setCourse(null);
                    savedUser.setDepartment(null);
                    savedUser.setOffice(dto.office());
                }
                savedUser.setType(dto.type());
            }

            this.userRepository.save(savedUser);
        } else if (dto.role().equals(Role.STUDENT_OFFICER)) {
            if (dto.yearLevel() == 0) throw new ForbiddenException("Please Select Student Year Level");

            if (dto.departmentClubRole().equals(ClubRole.STUDENT_OFFICER) && dto.socialClubRole().equals(ClubRole.STUDENT_OFFICER))
                throw new ForbiddenException("Multiple \"Student officer\" role in multiple departmentClub is Prohibited");

            if ((dto.departmentClubRole().equals(ClubRole.MEMBER) && dto.socialClubRole().equals(ClubRole.MEMBER)))
                throw new ForbiddenException("The Student Officer must be Officer to either Department or Social Club");

            var departmentClub = getClub(dto.departmentClubId(), Type.DEPARTMENT);
            var socialClub = getClub(dto.socialClubId(), Type.SOCIAL);

            // get the member role that equal to user officer of the club and set to member
            if (dto.departmentClubRole().equals(ClubRole.STUDENT_OFFICER)) {
                var memberRoles = this.memberRoleRepository.findMemberRoleByClubId(departmentClub.getId(), Role.STUDENT_OFFICER);
                unregisterTheStudentFromBeingOfficer(memberRoles);
            } else {
                var memberRoles = this.memberRoleRepository.findMemberRoleByClubId(socialClub.getId(), Role.STUDENT_OFFICER);
                unregisterTheStudentFromBeingOfficer(memberRoles);
            }

            savedUser.setYearLevel(dto.yearLevel());
            savedUser.setDepartment(getDepartment(dto.departmentId()));
            savedUser.setCourse(getCourse(dto.courseId()));

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
            this.userRepository.save(savedUser);
        } else if (dto.role().equals(Role.STUDENT)) {
            if (dto.yearLevel() == 0) throw new ForbiddenException("Please Select Student Year Level");

            savedUser.setYearLevel(dto.yearLevel());
            savedUser.setDepartment(getDepartment(dto.departmentId()));
            savedUser.setCourse(getCourse(dto.courseId()));

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
            this.userRepository.save(savedUser);
        } else if (UserUtil.getLabInChargeRoles().contains(dto.role())) {
            var department = getDepartment(dto.departmentId());
            var course = getCourse(dto.courseId());
            savedUser.setDepartment(department);
            savedUser.setCourse(course);
            this.userRepository.save(savedUser);
        } else {
            throw new ForbiddenException("Please Select a Valid Role");
        }
    }

    @Transactional
    @Override
    public void createUser(UserRegisterRequestDto dto) {
        if (dto.role().equals(Role.SUPER_ADMIN))
            throw new ForbiddenException("Invalid Role");

        if (!UserRegex.validateStudentUsername(dto.username()))
            throw new ForbiddenException("User ID is Invalid Format");

        // check if the user is existing already
        if (this.userRepository.existsByUsername(dto.username()))
            throw new ConflictException("User ID is existing already");

        // check if the email is existing already
        if (this.userRepository.existsByEmail(dto.email()))
            throw new ConflictException("Email is existing already");

        // check if the email is existing already
        if (this.userRepository.existsByContactNumber(dto.contactNumber()))
            throw new ConflictException("Contact Number is existing already");

        // validate contact number
        if (!UserUtil.validateContactNumber(dto.contactNumber()))
            throw new ForbiddenException("Invalid Contact Number");

        // validate gmail
        if (!UserUtil.validateGmail(dto.email()))
            throw new ForbiddenException("Invalid Gmail");

        var user = this.userMapper.toUser(dto);

        var savedUser = this.userRepository.save(user);

        String jwtToken = this.jwtService.generateRefreshToken(savedUser);
        this.emailService.sendEmail(UserUtil.removeWhiteSpace(savedUser.getEmail()), jwtToken);

        if (isRoleIncluded(dto.role())) return;

        setUpUser(savedUser, dto);
    }

    private void unregisterTheStudentFromBeingOfficer(List<MemberRole> memberRoles) {
        var filteredMemberRoles = memberRoles.stream().filter(m -> m.getRole().equals(ClubRole.STUDENT_OFFICER)).toList();
        if (!filteredMemberRoles.isEmpty()) {
            var updatedUserRoles = filteredMemberRoles.stream().peek(uu -> uu.setRole(ClubRole.MEMBER)).toList();
            this.memberRoleRepository.saveAll(updatedUserRoles);

            // check the user's member roles of each user
            for (var tempUser : filteredMemberRoles) {
                var userForValidation = tempUser.getUser();
                boolean thisUserShouldStillBeOfficer = false;
                for (var tempUserMemberRole : userForValidation.getMemberRoles()) {
                    if (tempUserMemberRole.getRole().equals(ClubRole.STUDENT_OFFICER)) {
                        thisUserShouldStillBeOfficer = true;
                        break;
                    }
                }

                // set the user's role into Student because this user is no longer officer in any club
                if (!thisUserShouldStillBeOfficer) {
                    userForValidation.setRole(Role.STUDENT);
                    this.userRepository.save(userForValidation);
                }
            }
        }
    }

    private void unregisterPersonnelForBeingModerator(List<MemberRole> memberRoles) {
        var filteredMemberRoles = memberRoles.stream().filter(m -> m.getRole().equals(ClubRole.MODERATOR)).toList();
        if (!filteredMemberRoles.isEmpty()) {
            for (var filteredRole : filteredMemberRoles) {
                var user = filteredRole.getUser();
                user.setRole(Role.PERSONNEL);

                filteredRole.setUser(null);
                filteredRole.setClub(null);
                this.memberRoleRepository.delete(filteredRole);
                this.userRepository.save(user);
            }
        }
    }

    // these roles don't need department, course, departmentClub and etc
    private boolean isRoleIncluded(Role role) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN);
        roles.add(Role.DSA);
        roles.add(Role.PRESIDENT);
        roles.add(Role.FINANCE);
        roles.add(Role.OFFICE_HEAD);
        roles.add(Role.GUIDANCE);
        roles.add(Role.CASHIER);
        roles.add(Role.LIBRARIAN);
        roles.add(Role.SCHOOL_NURSE);
        roles.add(Role.REGISTRAR);
        roles.add(Role.ACCOUNTING_CLERK);
        roles.add(Role.CUSTODIAN);
        roles.add(Role.VPAF);
        roles.add(Role.VPA);
        roles.add(Role.MULTIMEDIA);
        roles.add(Role.CHAPEL);
        roles.add(Role.PPLO);
        roles.add(Role.AUXILIARY_SERVICE_HEAD);
        return roles.contains(role);
    }

    public Club getClub(int id, Type type) {
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
    public UserInfoResponseDto getUserByUsername(int id) {
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

    @Transactional
    @Override
    public String authenticate(LoginRequestDto dto, HttpServletResponse response, boolean isWebsite) {
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

            if (user.isFirstTimeLogin())
                throw new UnauthorizedException("Check your Registered Email to Change Password to Continue using the System");

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
            if (!isWebsite) {
                if (UserUtil.getRoleThatCantLoginToFingerprint().contains(user.getRole()))
                    throw new UnauthorizedException("You are not allowed to access fingerprint");
                return "Bearer " + refreshToken;
            } else {
                return "Bearer " + accessToken;
            }
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

    @Transactional
    @Override
    public void forgotPassword(String email) {
        // check if the email doesn't exist already
        if (!this.userRepository.existsByEmail(email))
            throw new ForbiddenException("Invalid Email Address");

        var user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Email Address"));

        final String defaultPassword = "1234";
        user.setPassword(this.passwordEncoder.encode(defaultPassword));
        user.setFirstTimeLogin(true);
        var updatedUser = this.userRepository.save(user);

        String token = this.jwtService.generateToken(updatedUser); // 10 mins Access Token
        this.emailService.sendEmail(UserUtil.removeWhiteSpace(updatedUser.getEmail()), token);
    }
}

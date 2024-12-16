package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final ClubMapper clubMapper;
    private final DepartmentMapper departmentMapper;
    private final CourseMapper courseMapper;
    private final MemberRoleUtil memberRoleUtil;


    public UserMapper(PasswordEncoder passwordEncoder, ClubMapper clubMapper, DepartmentMapper departmentMapper, CourseMapper courseMapper, MemberRoleUtil memberRoleUtil) {
        this.passwordEncoder = passwordEncoder;
        this.clubMapper = clubMapper;
        this.departmentMapper = departmentMapper;
        this.courseMapper = courseMapper;
        this.memberRoleUtil = memberRoleUtil;
    }

    public User toUser(UserRegisterRequestDto dto) {
        return User.builder()
                .firstName(dto.firstName())
                .middleName(dto.middleName())
                .lastname(dto.lastname())
                .username(dto.username())
                .email(dto.email())
                .isFirstTimeLogin(true)
                .password(this.passwordEncoder.encode("1234"))
                .role(dto.role())
                .build();
    }

    public UserInfoResponseDto toUserInfoResponseDto(User user) {
        var officerClub = this.memberRoleUtil.getClubOfOfficer(user.getMemberRoles());
        var memberClub = this.memberRoleUtil.getClubOfMember(user.getMemberRoles());
        return new UserInfoResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastname(),
                user.getUsername(),
                user.getRole(),
                user.getYearLevel() != null ? user.getYearLevel() : 0,
                user.getCreatedAt(),
                user.getLastModified(),
                user.getCourse() != null ? this.courseMapper.toCourseResponseDto(user.getCourse()) : null,
                user.getDepartment() != null ? this.departmentMapper.toDepartmentResponseDto(user.getDepartment()) : null,
                officerClub != null ? this.clubMapper.toClubInformationResponseDto(officerClub) : null,
                memberClub != null ? this.clubMapper.toClubInformationResponseDto(memberClub) : null,
                officerClub != null ? officerClub.getName() : "N/A"
        );
    }

    public List<UserInfoResponseDto> toUserInfoDtoList(List<User> users) {
        return users
                .stream()
                .map(this::toUserInfoResponseDto)
                .toList();
    }
}

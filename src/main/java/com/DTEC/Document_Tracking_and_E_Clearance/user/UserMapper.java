package com.DTEC.Document_Tracking_and_E_Clearance.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toUser(UserRegisterRequestDto dto) {
        return User.builder()
                .firstName(dto.firstName())
                .middleName(dto.middleName())
                .lastname(dto.lastname())
                .username(dto.username())
                .password(this.passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();
    }

    public UserInfoResponseDto toUserInfoResponseDto(User user) {
        return new UserInfoResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastname(),
                user.getBirthDate(),
                user.getAddress(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt(),
                user.getLastModified()
        );
    }

    public List<UserInfoResponseDto> toUserInfoDtoList(List<User> users) {
        return users
                .stream()
                .map(this::toUserInfoResponseDto)
                .toList();
    }
}

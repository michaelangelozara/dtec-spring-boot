package com.DTEC.Document_Tracking_and_E_Clearance.user;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {

    String authenticate(LoginRequestDto dto, HttpServletResponse response, boolean isWebsite);

    void createUser(UserRegisterRequestDto dto);

    UserInfoResponseDto getUserByUsername(int id);

    List<UserInfoResponseDto> getAllUsers(int s, int e);

    UserInfoResponseDto me();

    void deleteUser(int id);

    String resetPassword(int userId);

    void update(UserRegisterRequestDto dto, int userId);

    DetailedUserResponseDto getUserById(int id);

    List<UserInfoResponseDto> searchUsers(String searchTerm);

    void changePassword(String password1, String password2, String token);

    String getModeratorStudentOfficerESignature();

    void forgotPassword(String email);

    void verifyToken(String token);
}

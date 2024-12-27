package com.DTEC.Document_Tracking_and_E_Clearance.user;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {

    String authenticate(LoginRequestDto dto, HttpServletResponse response);

    void createUser(UserRegisterRequestDto dto);

    UserInfoResponseDto getUserById(int id);

    List<UserInfoResponseDto> getAllUsers(int s, int e);

    UserInfoResponseDto me();

    String deleteUser(int id);

    String resetPassword(int userId);

    String update(UserRegisterRequestDto dto);
}

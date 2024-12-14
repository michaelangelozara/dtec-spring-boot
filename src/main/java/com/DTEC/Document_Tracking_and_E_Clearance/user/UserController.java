package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public UserController(UserService userService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.userService = userService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getCurrentUser() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.userService.me(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                ));
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final UserService userService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public AdminController(UserService userService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.userService = userService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserInfoResponseDto>>> getAllUser(
            @RequestParam(name = "s", defaultValue = "0") int s,
            @RequestParam(name = "e", defaultValue = "30") int e
    ){
        if(s > e)
            throw new ForbiddenException("Invalid range: 's' (start) must be less than or equal to 'e' (end)");

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "User List Successfully Fetched",
                        this.userService.getAllUsers(s, e),
                        "",
                        this.dateTimeFormatterUtil.getDateTime()
                )
        );
    }

    @PostMapping("/add-user")
    public ResponseEntity<ApiResponse<Void>> addUser(
            @Validated @RequestBody UserRegisterRequestDto dto
    ){
        this.userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
          new ApiResponse<>(
                  true,
                  "New User Successfully Created",
                  null,
                  "",
                  this.dateTimeFormatterUtil.getDateTime())
        );
    }

    @DeleteMapping("/users/delete/{user-id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable("user-id") int id
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.userService.deleteUser(id),
                        "",
                        this.dateTimeFormatterUtil.getDateTime()
                )
        );
    }
}

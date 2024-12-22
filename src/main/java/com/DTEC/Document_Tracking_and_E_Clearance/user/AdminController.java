package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.ClearanceService;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubService;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseService;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentResponseDto;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentService;
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
    private final DepartmentService departmentService;
    private final ClubService clubService;
    private final CourseService courseService;
    private final ClearanceService clearanceService;

    public AdminController(UserService userService, DateTimeFormatterUtil dateTimeFormatterUtil, DepartmentService departmentService, ClubService clubService, CourseService courseService, ClearanceService clearanceService) {
        this.userService = userService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.departmentService = departmentService;
        this.clubService = clubService;
        this.courseService = courseService;
        this.clearanceService = clearanceService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserInfoResponseDto>>> getAllUser(
            @RequestParam(name = "s", defaultValue = "0") int s,
            @RequestParam(name = "e", defaultValue = "10") int e
    ) {
        if (s > e)
            throw new ForbiddenException("Invalid range: 's' (start) must be less than or equal to 'e' (end)");

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "User List Successfully Fetched",
                        this.userService.getAllUsers(s, e),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @PostMapping("/add-user")
    public ResponseEntity<ApiResponse<Void>> addUser(
            @Validated @RequestBody UserRegisterRequestDto dto
    ) {
        this.userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "New User Successfully Created",
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }

    @DeleteMapping("/users/delete/{user-id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable("user-id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.userService.deleteUser(id),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @PostMapping("/users/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestParam(name = "id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.userService.resetPassword(id),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping("/clubs")
    public ResponseEntity<ApiResponse<List<ClubResponseDto>>> getAllClubs(
            @RequestParam(name = "s", defaultValue = "0") int s,
            @RequestParam(name = "e", defaultValue = "30") int e
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Successfully Fetched All Clubs",
                        this.clubService.getAllClubs(s, e),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDto>>> getAllDepartments() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(true,
                        "Successfully Fetched all Departments",
                        this.departmentService.getAllDepartment(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getAllCourses() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(true,
                        "Successfully Fetched all Courses",
                        this.courseService.getAllCourse(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }

    @PostMapping("/students/release-clearances")
    public ResponseEntity<ApiResponse<Void>> releaseAllStudentsClearances() {
        String response = this.clearanceService.releaseStudentClearances();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        response,
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @PostMapping("/personnel/release-clearances")
    public ResponseEntity<ApiResponse<Void>> releaseAllPersonnelClearances() {
        String response = this.clearanceService.releasePersonnelClearances();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        response,
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }
}

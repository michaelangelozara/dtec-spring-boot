package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clearances")
public class clearanceController {

    private final ClearanceService clearanceService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public clearanceController(ClearanceService clearanceService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.clearanceService = clearanceService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @PostMapping("/students/sign-clearance/{clearance-id}")
    public ResponseEntity<ApiResponse<String>> studentSignClearance(
            @PathVariable("clearance-id") int id,
            @RequestBody Map<String, String> signatureMap
    ) {
        String signature = signatureMap.get("signature");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.clearanceService.studentSignClearance(id, signature),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClearanceResponseDto>>> getAllClearance() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Successfully Fetched All Clearances",
                        this.clearanceService.getAllClearances(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping("/students/get-all-clearances")
    public ResponseEntity<ApiResponse<List<ClearanceResponseDto>>> getAllStudentClearances() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Successfully Fetched All Student's Clearance",
                        this.clearanceService.getAllStudentClearances(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping("/new-clearance")
    public ResponseEntity<ApiResponse<ClearanceResponseDto>> getStudentClearance() {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Successfully Fetched All Student's Clearance",
                        this.clearanceService.getNewClearance(),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @PostMapping("/{clearance-id}/on-click")
    public ResponseEntity<ApiResponse<Void>> clearanceOnClick(
            @PathVariable("clearance-id") int id
    ) {
        this.clearanceService.onClick(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Clearance Signoff Status is now In-Progress",
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @PreAuthorize("hasAnyRole(" +
            "'GUIDANCE', " +
            "'DEAN', " +
            "'CASHIER', " +
            "'LIBRARIAN', " +
            "'SCHOOL_NURSE'," +
            "'PROGRAM_HEAD'," +
            "'REGISTRAR', " +
            "'DSA'," +
            "'ACCOUNTING_CLERK', " +
            "'CUSTODIAN', " +
            "'VPAF', " +
            "'VPA', " +
            "'MULTIMEDIA', " +
            "'SCIENCE_LAB', " +
            "'COMPUTER_SCIENCE_LAB', " +
            "'ELECTRONICS_LAB', " +
            "'CRIM_LAB', " +
            "'HRM_LAB', " +
            "'NURSING_LAB', " +
            "'FINANCE'," +
            "'PRESIDENT')")
    @PostMapping("/sign-clearance/{clearance-id}")
    public ResponseEntity<ApiResponse<Void>> signClearance(
            @PathVariable("clearance-id") int id,
            @RequestBody Map<String, String> signatureMap
    ) {
        String signature = signatureMap.get("signature");
        String response = this.clearanceService.signClearance(id, signature);
        return ResponseEntity.status(HttpStatus.CREATED).body(
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

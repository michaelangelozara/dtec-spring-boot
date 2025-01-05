package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/implementation-letter-in-campuses")
@EnableMethodSecurity
public class ImplementationLetterInCampusController {

    private final ImplementationLetterInCampusService implementationLetterInCampusService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public ImplementationLetterInCampusController(ImplementationLetterInCampusService implementationLetterInCampusService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.implementationLetterInCampusService = implementationLetterInCampusService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','STUDENT_OFFICER')")
    @PostMapping("/request-letter")
    public ResponseEntity<ApiResponse<Void>> requestLetter(
            @RequestBody ImplementationLetterInCampusRequestDto dto
    ) {
        this.implementationLetterInCampusService.requestImplementationLetter(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Implementation Letter Submitted", null, "", this.dateTimeFormatterUtil.formatIntoDateTime()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR', 'STUDENT_OFFICER', 'DSA', 'PRESIDENT', 'FINANCE', 'OFFICE_HEAD')")
    public ResponseEntity<ApiResponse<ImplementationLetterInCampusResponseDto>> getImplementationLetter(
            @PathVariable("id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.implementationLetterInCampusService.getImplementationLetter(id),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }
}

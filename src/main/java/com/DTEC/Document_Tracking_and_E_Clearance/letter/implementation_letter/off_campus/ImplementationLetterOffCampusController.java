package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/implementation-letter-out-campus")
@EnableMethodSecurity
public class ImplementationLetterOffCampusController {

    private final ImplementationLetterOffCampusService implementationLetterOffCampusService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public ImplementationLetterOffCampusController(ImplementationLetterOffCampusService implementationLetterOffCampusService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.implementationLetterOffCampusService = implementationLetterOffCampusService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STUDENT_OFFICER')")
    @PostMapping("/request-letter")
    public ResponseEntity<ApiResponse<Void>> addImplementationLetter(
            @RequestBody ImplementationLetterOffCampusRequestDto dto
    ){
        this.implementationLetterOffCampusService.requestImplementationLetter(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Implementation Letter Submitted", null, "", this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }

}

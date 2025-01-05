package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/communication-letters")
@EnableMethodSecurity
public class CommunicationLetterController {

    private final CommunicationLetterService communicationLetterService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public CommunicationLetterController(CommunicationLetterService communicationLetterService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.communicationLetterService = communicationLetterService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @PostMapping("/request-letter")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STUDENT_OFFICER')")
    public ResponseEntity<ApiResponse<Void>> addCommunicationLetter(
            @RequestBody CommunicationLetterRequestDto dto,
            @RequestParam(name = "type") CommunicationLetterType type
    ) {
        this.communicationLetterService.requestLetter(dto, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Communication Letter Submitted",
                        null, "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<List<CommunicationLetterResponseDto>>> getAllCommunicationLetter(
            @RequestParam(name = "s", defaultValue = "0") int s,
            @RequestParam(name = "e", defaultValue = "10") int e,
            @RequestParam(name = "type", defaultValue = "IN_CAMPUS") CommunicationLetterType type

    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(true,
                        "Successfully Fetched Communication Letters",
                        this.communicationLetterService.getAllCommunicationLetter(s, e, type),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR','STUDENT_OFFICER', 'DSA', 'PRESIDENT', 'FINANCE', 'OFFICE_HEAD')")
    public ResponseEntity<ApiResponse<CommunicationLetterResponseDto>> getCommunicationLetter(
            @PathVariable("id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Communication Letter Successfully Fetch",
                        this.communicationLetterService.getCommunicationLetter(id),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }
}

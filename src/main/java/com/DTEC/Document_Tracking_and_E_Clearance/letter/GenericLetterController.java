package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/generic-letters")
@EnableMethodSecurity
public class GenericLetterController {

    private final GenericLetterService genericLetterService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public GenericLetterController(GenericLetterService genericLetterService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.genericLetterService = genericLetterService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<List<GenericResponse>>> getLetters(
            @RequestParam(name = "type", defaultValue = "FOR_EVALUATION") LetterStatus status
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.genericLetterService.getAllLetters(status),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }
}

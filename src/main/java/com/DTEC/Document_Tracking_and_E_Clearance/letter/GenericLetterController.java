package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/on-click/{letter-id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> onClick(
            @PathVariable("letter-id") int id,
            @RequestParam(name = "type") TypeOfLetter type
    ) {
        this.genericLetterService.onClick(type, id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Letter is on-progress already",
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }

    @PostMapping("/sign-letter/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> signLetter(
            @PathVariable("id") int id,
            @RequestParam(name = "type") TypeOfLetter type,
            @RequestBody Map<String, String> signature
    ) {
        String tempSignature = signature.get("signature");
        this.genericLetterService.signLetter(type, tempSignature, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Signature has been Attached",
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }
}

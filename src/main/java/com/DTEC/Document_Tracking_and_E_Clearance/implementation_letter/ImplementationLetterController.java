package com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/implementation-letters")
public class ImplementationLetterController {

    private final ImplementationLetterService implementationLetterService;

    public ImplementationLetterController(ImplementationLetterService implementationLetterService) {
        this.implementationLetterService = implementationLetterService;
    }

    @PostMapping("/request-letter")
    public ResponseEntity<String> requestLetter(
            @RequestBody ImplementationLetterDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.implementationLetterService.addImplementationLetter(dto));
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ioops")
public class IOPPController {

    private final IOPPService ioppService;

    public IOPPController(IOPPService ioppService) {
        this.ioppService = ioppService;
    }

    @PostMapping("/add-ioop")
    public ResponseEntity<String> addIoop(
            @RequestBody IOPPRequestDto dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.ioppService.requestLetter(dto));
    }
}

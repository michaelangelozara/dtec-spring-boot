package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permit-to-enters")
public class PermitToEnterController {

    private final PermitToEnterService permitToEnterService;

    public PermitToEnterController(PermitToEnterService permitToEnterService) {
        this.permitToEnterService = permitToEnterService;
    }

    @PostMapping("/request-permit-to-enter")
    @PreAuthorize("hasRole('STUDENT_OFFICER')")
    public ResponseEntity<ApiResponse<Void>> requestLetter(
            @RequestBody PermitToEnterRequestDto dto
    ) {
        this.permitToEnterService.requestLetter(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Letter has been Submitted",
                                null,
                                "",
                                ""
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermitToEnterResponseDto>> getLetter(
            @PathVariable("id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        new ApiResponse<>(
                                true,
                                "Letter is Successfully Fetched",
                                this.permitToEnterService.getPermitToEnterById(id),
                                "",
                                ""
                        )
                );
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sfefs")
public class SFEFController {

    private final SFEFService sfefService;

    public SFEFController(SFEFService sfefService) {
        this.sfefService = sfefService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('STUDENT_OFFICER')")
    public ResponseEntity<ApiResponse<Void>> add(
            @RequestBody SFEFRequestDto dto
    ){
        this.sfefService.requestLetter(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Letter has been Sent",
                                null,
                                "",
                                ""
                        )
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SFEFResponseDto>> getSFEFById(
            @PathVariable("id") int id
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Letter is successfully Fetched",
                                this.sfefService.getSFEFById(id),
                                "",
                                ""
                        )
                );
    }
}

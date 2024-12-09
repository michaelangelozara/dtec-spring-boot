package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clearances")
public class clearanceController {

    private final ClearanceService clearanceService;

    public clearanceController(ClearanceService clearanceService) {
        this.clearanceService = clearanceService;
    }

    @GetMapping
    public ResponseEntity<List<ClearanceResponseDto>> getAllClearance(
            @RequestParam(value = "n", defaultValue = "50") int n
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(this.clearanceService.getAllClearances(n));
    }

    @GetMapping("/students/{student-id}")
    public ResponseEntity<ClearanceResponseDto> getClearanceByStudentId(
            @PathVariable("student-id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(this.clearanceService.getClearanceByStudentId(id));
    }

    @PostMapping("/release-clearances")
    public ResponseEntity<String> releaseAllClearances() {
        return ResponseEntity.status(HttpStatus.OK).body(this.clearanceService.releaseClearances());
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.club;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping
    public ResponseEntity<List<ClubResponseDto>> getAllClubs() {
        return ResponseEntity.status(HttpStatus.OK).body(this.clubService.getAllClubs());
    }

    @PostMapping("/add-club")
    public ResponseEntity<String> addClub(@RequestBody AddClubRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.clubService.addClub(dto));
    }
}

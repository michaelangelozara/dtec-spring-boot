package com.DTEC.Document_Tracking_and_E_Clearance.club;

import java.util.List;

public interface ClubService {

    ClubResponseDto getClubById(int id);

    String addClubForStudent(int clubId, int studentId);

    List<ClubResponseDto> getAllClubs();

    String addClub(AddClubRequestDto dto);
}

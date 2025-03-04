package com.DTEC.Document_Tracking_and_E_Clearance.club;

import java.util.List;

public interface ClubService {

    ClubResponseDto getClubById(int id);

    List<ClubResponseDto> getAllClubs(int s, int e);

    void updateLogo(String image, int clubId);
}

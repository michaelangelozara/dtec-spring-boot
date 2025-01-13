package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

public interface PermitToEnterService {

    void add(PermitToEnterRequestDto dto);

    PermitToEnterResponseDto getPermitToEnterById(int id);
}

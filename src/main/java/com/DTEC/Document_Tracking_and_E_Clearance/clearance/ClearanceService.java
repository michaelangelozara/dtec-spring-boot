package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import java.util.List;

public interface ClearanceService {

    String releaseClearances();

    String generateClearanceByUserId(int id);

    List<ClearanceResponseDto> getAllClearances(int n);

    ClearanceResponseDto getClearanceByStudentId(int id);

    String signClearance(SignClearanceRequestDto dto);
}

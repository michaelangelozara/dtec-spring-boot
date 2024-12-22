package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import java.util.List;

public interface ClearanceService {

    String releaseStudentClearances();

    String releasePersonnelClearances();

    List<ClearanceResponseDto> getAllClearances();

    List<ClearanceResponseDto> getAllStudentClearances();

    String signClearance(int clearanceId, String signature);

    ClearanceResponseDto getNewClearance();

    void onClick(int clearanceId);

    String studentSignClearance(int clearanceId, String signature);

}

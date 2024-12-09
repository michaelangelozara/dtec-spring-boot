package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import java.util.List;

public record IOPPRequestDto(
        String title,
        String rationale,
        String targetGroup,
        String dateAndPlace,
        String programOrFlowOfActivity,
        List<CAOORequestDto> caoos
) {
}

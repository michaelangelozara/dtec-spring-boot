package com.DTEC.Document_Tracking_and_E_Clearance.department;

public record AddDepartmentRequestDto(
        String name,
        byte[] logo
) {
}

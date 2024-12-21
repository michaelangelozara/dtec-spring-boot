package com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClearanceSignoffMapper {

    public ClearanceSignoffResponseDto toClearanceSignoffResponseDto(
            ClearanceSignoff clearanceSignoff
    ){
        return new ClearanceSignoffResponseDto(
                clearanceSignoff.getId(),
                clearanceSignoff.getLastModified(),
                clearanceSignoff.getSignature(),
                clearanceSignoff.getRole(),
                clearanceSignoff.getStatus(),
                clearanceSignoff.getNote(),
                !clearanceSignoff.getUser().getMiddleName().isEmpty() ? clearanceSignoff.getUser().getFirstName() + " " + clearanceSignoff.getUser().getMiddleName().charAt(0) + ". " + clearanceSignoff.getUser().getLastname() : clearanceSignoff.getUser().getFirstName() + " " + clearanceSignoff.getUser().getLastname()
        );
    }

    public List<ClearanceSignoffResponseDto> toClearanceSignoffResponseDtoList(
            List<ClearanceSignoff> clearanceSignoffs
    ){
        return clearanceSignoffs
                .stream()
                .map(this::toClearanceSignoffResponseDto)
                .toList();
    }
}

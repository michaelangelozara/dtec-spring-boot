package com.DTEC.Document_Tracking_and_E_Clearance.club;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubMapper {

    public ClubResponseDto toClubInformationResponseDto(Club club){
        return new ClubResponseDto(
                club.getId(),
                club.getName(),
                club.getType(),
                club.getCreatedAt(),
                club.getLastModified()
        );
    }

    public List<ClubResponseDto> toClubInformationResponseDtoList(List<Club> clubs){
        return clubs
                .stream()
                .map(this::toClubInformationResponseDto)
                .toList();

    }
}

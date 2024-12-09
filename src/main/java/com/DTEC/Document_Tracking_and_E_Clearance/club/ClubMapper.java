package com.DTEC.Document_Tracking_and_E_Clearance.club;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class ClubMapper {

    public Club toClub(AddClubRequestDto dto){
        return Club.builder()
                .name(dto.name())
                .logo(Base64.getEncoder().encodeToString(dto.logo()))
                .build();
    }


    public ClubResponseDto toClubResponseDto(Club club){
        return new ClubResponseDto(
                club.getId(),
                club.getName(),
                club.getLogo(),
                club.getCreatedAt(),
                club.getLastModified()
        );
    }

    public List<ClubResponseDto> clubResponseDtoList(List<Club> clubs){
        return clubs
                .stream()
                .map(this::toClubResponseDto)
                .toList();

    }
}

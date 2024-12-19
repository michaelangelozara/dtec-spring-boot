package com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SignedPeopleMapper {

    public SignedPeopleResponseDto toSignedPeopleResponseDto(SignedPeople signedPeople){
        return new SignedPeopleResponseDto(
                signedPeople.getUser() != null ? signedPeople.getUser().getId() : null,
                signedPeople.getRole(),
                signedPeople.getSignature(),
                signedPeople.getStatus()
        );
    }

    public List<SignedPeopleResponseDto> toSignedPeopleResponseDtoList(List<SignedPeople> signedPeople){
        return signedPeople
                .stream()
                .map(this::toSignedPeopleResponseDto)
                .toList();
    }
}

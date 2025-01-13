package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import org.springframework.stereotype.Service;

@Service
public class PermitToEnterMapper {

    public PermitToEnter toPermitToEnter(PermitToEnterRequestDto dto) {
        return PermitToEnter.builder()
                .activity(dto.activity())
                .date(dto.date())
                .timeFrom(dto.timeFrom())
                .timeTo(dto.timeTo())
                .type(TypeOfLetter.PERMIT_TO_ENTER)
                .status(LetterStatus.FOR_EVALUATION)
                .participants(dto.participants())
                .build();
    }

    public PermitToEnterResponseDto toPermitToEnterResponseDto(PermitToEnter permitToEnter){
        return new PermitToEnterResponseDto(
                permitToEnter.getId(),
                permitToEnter.getActivity(),
                permitToEnter.getDate(),
                permitToEnter.getTimeFrom(),
                permitToEnter.getTimeTo(),
                permitToEnter.getParticipants()
        );
    }
}

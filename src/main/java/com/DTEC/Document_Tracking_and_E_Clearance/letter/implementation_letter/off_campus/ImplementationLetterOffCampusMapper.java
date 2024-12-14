package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.stereotype.Service;

@Service
public class ImplementationLetterOffCampusMapper {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public ImplementationLetterOffCampusMapper(DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    public ImplementationLetterOffCampusResponseDto toImplementationLetterOffCampusResponseDto(
            ImplementationLetterOffCampus implementationLetterOffCampus
    ) {
        var studentOfficer = implementationLetterOffCampus.getStudentOfficer();
        var moderator = implementationLetterOffCampus.getModerator();

        return new ImplementationLetterOffCampusResponseDto(
                implementationLetterOffCampus.getId(),
                implementationLetterOffCampus.getTitleOfActivity(),
                implementationLetterOffCampus.getDescription(),
                implementationLetterOffCampus.getReasons(),
                this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterOffCampus.getDateAndTimeOfImplementation()),
                implementationLetterOffCampus.getProgramOrFlow(),
                implementationLetterOffCampus.getStudentOfficerSignature(),
                implementationLetterOffCampus.getCreatedAt() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterOffCampus.getCreatedAt()) : "N/A",
                implementationLetterOffCampus.getLastModified() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterOffCampus.getLastModified()) : "N/A",
                implementationLetterOffCampus.getStatus(),
                implementationLetterOffCampus.getType(),
                implementationLetterOffCampus.getCaoos(),
                implementationLetterOffCampus.getClub() != null ? implementationLetterOffCampus.getClub().getName() : "N/A",
                moderator != null ? moderator.getFirstName() + " " + moderator.getMiddleName() + " " + moderator.getLastname() : "N/A",
                studentOfficer != null ? studentOfficer.getFirstName() + " " + studentOfficer.getMiddleName() + " " + studentOfficer.getLastname() : "N/A",
                implementationLetterOffCampus.getModeratorSignature()

        );
    }

    public ImplementationLetterOffCampus toImplementationLetterOutCampus(
            ImplementationLetterOffCampusRequestDto dto
    ) {
        return ImplementationLetterOffCampus.builder()
                .titleOfActivity(dto.title())
                .description(dto.description())
                .reasons(dto.reason())
                .dateAndTimeOfImplementation(this.dateTimeFormatterUtil.formatIntoDateTime(dto.dateAndTime()))
                .programOrFlow(dto.programOrFlowOfActivity())
                .studentOfficerSignature(dto.signature())
                .type(TypeOfLetter.IMPLEMENTATION_LETTER_OFF_CAMPUS)
                .status(LetterStatus.PENDING)
                .build();
    }
}

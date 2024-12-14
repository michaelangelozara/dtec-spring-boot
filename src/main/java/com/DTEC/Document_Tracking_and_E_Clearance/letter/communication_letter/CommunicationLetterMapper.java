package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationLetterMapper {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public CommunicationLetterMapper(DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    public CommunicationLetterResponseDto toCommunicationLetterResponseDto(
            CommunicationLetter communicationLetter
    ) {
        var studentOfficer = communicationLetter.getStudentOfficer();
        var moderator = communicationLetter.getModerator();
        return new CommunicationLetterResponseDto(
                communicationLetter.getId(),
                communicationLetter.getDate(),
                communicationLetter.getLetterOfContent(),
                communicationLetter.getTypeOfCampus(),
                communicationLetter.getStatus(),
                communicationLetter.getCreatedAt() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(communicationLetter.getCreatedAt()) : "N/A",
                communicationLetter.getLastModified() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(communicationLetter.getLastModified()) : "N/A",
                communicationLetter.getStudentOfficerSignature(),
                communicationLetter.getModeratorSignature(),
                moderator != null ? moderator.getFirstName() + " " + moderator.getMiddleName() + " " + moderator.getLastname() : "N/A",
                studentOfficer != null ? studentOfficer.getFirstName() + " " + studentOfficer.getMiddleName() + " " + studentOfficer.getLastname() : "N/A"
        );
    }

    public List<CommunicationLetterResponseDto> toCommunicationLetterResponseDtoList(
            List<CommunicationLetter> communicationLetters
    ) {
        return communicationLetters
                .stream()
                .map(this::toCommunicationLetterResponseDto)
                .toList();
    }
}

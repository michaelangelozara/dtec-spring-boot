package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationLetterMapper {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final SignedPeopleMapper signedPeopleMapper;

    public CommunicationLetterMapper(DateTimeFormatterUtil dateTimeFormatterUtil, SignedPeopleMapper signedPeopleMapper) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.signedPeopleMapper = signedPeopleMapper;
    }

    public CommunicationLetterResponseDto toCommunicationLetterResponseDto(
            CommunicationLetter communicationLetter
    ) {
        var studentOfficer = communicationLetter.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.STUDENT_OFFICER)).findFirst();
        var moderator = communicationLetter.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.MODERATOR)).findFirst();
        var dsa = communicationLetter.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.DSA)).findFirst();
        var officeHead = communicationLetter.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.OFFICE_HEAD)).findFirst();
        var president = communicationLetter.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.PRESIDENT)).findFirst();
        return new CommunicationLetterResponseDto(
                communicationLetter.getId(),
                communicationLetter.getDate(),
                communicationLetter.getLetterOfContent(),
                communicationLetter.getTypeOfCampus(),
                communicationLetter.getStatus(),
                communicationLetter.getCreatedAt() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(communicationLetter.getCreatedAt()) : "N/A",
                communicationLetter.getLastModified() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(communicationLetter.getLastModified()) : "N/A",
                studentOfficer.isPresent() ? studentOfficer.get().getSignature() : "N/A",
                moderator.isPresent() ? moderator.get().getSignature() : "N/A",
                moderator.isPresent() ? moderator.get().getUser().getFirstName() + " " + moderator.get().getUser().getMiddleName() + " " + moderator.get().getUser().getLastname() : "N/A",
                studentOfficer.isPresent() ? studentOfficer.get().getUser().getFirstName() + " " + studentOfficer.get().getUser().getMiddleName() + " " + studentOfficer.get().getUser().getLastname() : "N/A",
                communicationLetter.getCurrentLocation(),
                this.signedPeopleMapper.toSignedPeopleResponseDtoList(communicationLetter.getSignedPeople())
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

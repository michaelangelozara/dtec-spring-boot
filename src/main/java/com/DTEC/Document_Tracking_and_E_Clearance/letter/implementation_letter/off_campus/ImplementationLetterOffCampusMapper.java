package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class ImplementationLetterOffCampusMapper {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final SignedPeopleMapper signedPeopleMapper;

    public ImplementationLetterOffCampusMapper(DateTimeFormatterUtil dateTimeFormatterUtil, SignedPeopleMapper signedPeopleMapper) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.signedPeopleMapper = signedPeopleMapper;
    }

    public ImplementationLetterOffCampusResponseDto toImplementationLetterOffCampusResponseDto(
            ImplementationLetterOffCampus implementationLetterOffCampus
    ) {
        var studentOfficer = implementationLetterOffCampus.getSignedPeople()
                .stream()
                .filter(s -> s.getRole().equals(Role.STUDENT_OFFICER) && s.getUser() != null)
                .findFirst();

        var moderator = implementationLetterOffCampus.getSignedPeople()
                .stream()
                .filter(s -> s.getRole().equals(Role.MODERATOR) && s.getUser() != null)
                .findFirst();
        return new ImplementationLetterOffCampusResponseDto(
                implementationLetterOffCampus.getId(),
                implementationLetterOffCampus.getTitleOfActivity(),
                implementationLetterOffCampus.getDescription(),
                implementationLetterOffCampus.getReasons(),
                this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterOffCampus.getDateAndTimeOfImplementation()),
                implementationLetterOffCampus.getProgramOrFlow(),
                studentOfficer.isPresent() ? studentOfficer.get().getSignature() : "N/A",
                implementationLetterOffCampus.getCreatedAt() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterOffCampus.getCreatedAt()) : "N/A",
                implementationLetterOffCampus.getLastModified() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterOffCampus.getLastModified()) : "N/A",
                implementationLetterOffCampus.getStatus(),
                implementationLetterOffCampus.getType(),
                implementationLetterOffCampus.getCaoos(),
                implementationLetterOffCampus.getClub() != null ? implementationLetterOffCampus.getClub().getName() : "N/A",
                moderator.isPresent() ? moderator.get().getUser().getFirstName() + " " + moderator.get().getUser().getMiddleName() + " " + moderator.get().getUser().getLastname() : "N/A",
                studentOfficer.isPresent() ? studentOfficer.get().getUser().getFirstName() + " " + studentOfficer.get().getUser().getMiddleName() + " " + studentOfficer.get().getUser().getLastname() : "N/A",
                moderator.isPresent() ? moderator.get().getSignature() : "N/A",
                implementationLetterOffCampus.getCurrentLocation(),
                this.signedPeopleMapper.toSignedPeopleResponseDtoList(implementationLetterOffCampus.getSignedPeople())
        );
    }

    public ImplementationLetterOffCampus toImplementationLetterOffCampus(
            ImplementationLetterOffCampusRequestDto dto
    ) {
        // Parse the string as an Instant (UTC)
        ZonedDateTime utcDateTime = ZonedDateTime.parse(dto.dateAndTime());
        return ImplementationLetterOffCampus.builder()
                .titleOfActivity(dto.title())
                .description(dto.description())
                .reasons(dto.reason())
                .dateAndTimeOfImplementation(utcDateTime.toLocalDateTime())
                .programOrFlow(dto.programOrFlowOfActivity())
                .type(TypeOfLetter.IMPLEMENTATION_LETTER_OFF_CAMPUS)
                .currentLocation(CurrentLocation.COMMUNITY)
                .status(LetterStatus.IN_PROGRESS)
                .build();
    }
}

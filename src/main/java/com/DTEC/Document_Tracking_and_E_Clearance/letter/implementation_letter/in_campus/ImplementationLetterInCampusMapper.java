package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import org.springframework.stereotype.Service;

@Service
public class ImplementationLetterInCampusMapper {

    private final SchoolYearGenerator schoolYearGenerator;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final SignedPeopleMapper signedPeopleMapper;

    public ImplementationLetterInCampusMapper(SchoolYearGenerator schoolYearGenerator, DateTimeFormatterUtil dateTimeFormatterUtil, SignedPeopleMapper signedPeopleMapper) {
        this.schoolYearGenerator = schoolYearGenerator;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.signedPeopleMapper = signedPeopleMapper;
    }

    public ImplementationLetterInCampusResponseDto toImplementationLetterInCampusResponseDto(
            ImplementationLetterInCampus implementationLetterInCampus
    ) {
        var studentOfficer = implementationLetterInCampus.getSignedPeople()
                .stream()
                .filter(s -> s.getRole().equals(Role.STUDENT_OFFICER) && s.getUser() != null)
                .findFirst();

        var moderator = implementationLetterInCampus.getSignedPeople()
                .stream()
                .filter(s -> s.getRole().equals(Role.MODERATOR) && s.getUser() != null)
                .findFirst();
        return new ImplementationLetterInCampusResponseDto(
                implementationLetterInCampus.getId(),
                implementationLetterInCampus.getNameOfActivity(),
                implementationLetterInCampus.getSemesterAndSchoolYear(),
                implementationLetterInCampus.getTitle(),
                implementationLetterInCampus.getVenue(),
                implementationLetterInCampus.getDateTime(),
                implementationLetterInCampus.getExpectedOutput(),
                implementationLetterInCampus.getObjective(),
                implementationLetterInCampus.getProjectedExpense(),
                implementationLetterInCampus.getSourceOfFund(),
                implementationLetterInCampus.getCreatedAt() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterInCampus.getCreatedAt()) : "N/A",
                implementationLetterInCampus.getLastModified() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(implementationLetterInCampus.getLastModified()) : "N/A",
                implementationLetterInCampus.getParticipants(),
                implementationLetterInCampus.getRationale(),
                implementationLetterInCampus.getStatus(),
                studentOfficer.isPresent() ? studentOfficer.get().getSignature() : "N/A",
                moderator.isPresent() ? moderator.get().getSignature() : "N/A",
                implementationLetterInCampus.getType(),
                implementationLetterInCampus.getClub() != null ? implementationLetterInCampus.getClub().getName() : "N/A",
                studentOfficer.isPresent() ? studentOfficer.get().getUser().getFirstName() + " " + studentOfficer.get().getUser().getMiddleName() + " " + studentOfficer.get().getUser().getLastname() : "N/A",
                moderator.isPresent() ? moderator.get().getUser().getFirstName() + " " + moderator.get().getUser().getMiddleName() + " " + moderator.get().getUser().getLastname() : "N/A",
                implementationLetterInCampus.getCurrentLocation(),
                this.signedPeopleMapper.toSignedPeopleResponseDtoList(implementationLetterInCampus.getSignedPeople())
        );
    }

    public ImplementationLetterInCampus toImplementationLetter(ImplementationLetterInCampusRequestDto dto) {
        return ImplementationLetterInCampus.builder()
                .nameOfActivity(dto.nameOfActivity())
                .title(dto.title())
                .venue(dto.venue())
                .dateTime(this.dateTimeFormatterUtil.formatIntoDateTime(dto.dateTimes()))
                .objective(dto.objectives())
                .sourceOfFund(dto.sourceOfFunds())
                .projectedExpense(dto.projectedExpenses())
                .expectedOutput(dto.expectedOutputs())
                .status(LetterStatus.FOR_EVALUATION)
                .currentLocation(CurrentLocation.MODERATOR)
                .participants(dto.participants())
                .rationale(dto.rationale())
                .type(TypeOfLetter.IMPLEMENTATION_LETTER_IN_CAMPUS)
                .semesterAndSchoolYear(this.schoolYearGenerator.generateSchoolYear())
                .build();
    }
}

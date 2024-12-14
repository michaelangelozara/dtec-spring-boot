package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import org.springframework.stereotype.Service;

@Service
public class ImplementationLetterInCampusMapper {

    private final SchoolYearGenerator schoolYearGenerator;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public ImplementationLetterInCampusMapper(SchoolYearGenerator schoolYearGenerator, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.schoolYearGenerator = schoolYearGenerator;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    public ImplementationLetterInCampusResponseDto toImplementationLetterInCampusResponseDto(
            ImplementationLetterInCampus implementationLetterInCampus
    ) {
        var studentOfficer = implementationLetterInCampus.getStudentOfficer();
        var moderator = implementationLetterInCampus.getModerator();

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
                implementationLetterInCampus.getStudentOfficerSignature(),
                implementationLetterInCampus.getModeratorSignature(),
                implementationLetterInCampus.getType(),
                implementationLetterInCampus.getClub() != null ? implementationLetterInCampus.getClub().getName() : "N/A",
                studentOfficer != null ? studentOfficer.getFirstName() + " " + studentOfficer.getMiddleName() + " " + studentOfficer.getLastname() : "N/A",
                moderator != null ? moderator.getFirstName() + " " + moderator.getMiddleName() + " " + moderator.getLastname() : "N/A"
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
                .studentOfficerSignature(dto.signature())
                .participants(dto.participants())
                .rationale(dto.rationale())
                .type(TypeOfLetter.IMPLEMENTATION_LETTER_IN_CAMPUS)
                .semesterAndSchoolYear(this.schoolYearGenerator.generateSchoolYear())
                .build();
    }
}

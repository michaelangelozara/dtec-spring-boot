package com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.SchoolYear;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ImplementationLetterMapper {

    public ImplementationLetter toImplementationLetter(ImplementationLetterDto dto) {
        return ImplementationLetter.builder()
                .clubName(dto.clubName())
                .nameOfActivity(dto.nameOfActivity())
                .title(dto.title())
                .dateTime(dto.dateTime())
                .venue(dto.venue())
                .participants(dto.participants())
                .rationale(dto.rationale())
                .objectives(dto.objectives())
                .sourceOfFund(dto.sourceOfFund())
                .projectedExpenses(dto.projectedExpenses())
                .expectedOutput(dto.expectedOutput())
                .semesterAndSchoolYear(SchoolYear.generateSchoolYear())
                .signature(Base64.getEncoder().encodeToString(dto.signature()))
                .build();
    }
}

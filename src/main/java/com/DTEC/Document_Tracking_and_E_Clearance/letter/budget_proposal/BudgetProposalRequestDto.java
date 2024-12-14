package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity.ExpectedExpenseRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BudgetProposalRequestDto(
        String name,
        LocalDate date,
        String venue,
        @JsonProperty("source_of_fund") String sourceOfFund,
        @JsonProperty("allotted_amount") BigDecimal allottedAmount,
        @JsonProperty("expected_expenses") List<ExpectedExpenseRequestDto> expectedExpenses,
        @JsonProperty("student_officer_signature") String studentOfficerSignature

) {
}

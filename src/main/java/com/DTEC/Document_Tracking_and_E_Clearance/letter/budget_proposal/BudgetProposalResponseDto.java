package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity.ExpectedExpense;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BudgetProposalResponseDto(
        int id,
        String name,
        LocalDate date,
        String venue,
        @JsonProperty("source_of_fund") String sourceOfFund,
        @JsonProperty("allotted_amount") BigDecimal allottedAmount,
        LetterStatus status,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("last_modified") String lastModified,
        @JsonProperty("student_officer_signature") String studentOfficerSignature,
        @JsonProperty("moderator_signature") String moderatorSignature,
        @JsonProperty("student_officer") String studentOfficer,
        String moderator,
        List<ExpectedExpense> expectedExpenses
) {
}

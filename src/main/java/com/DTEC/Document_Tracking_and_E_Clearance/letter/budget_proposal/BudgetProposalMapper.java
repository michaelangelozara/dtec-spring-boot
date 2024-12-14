package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetProposalMapper {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public BudgetProposalMapper(DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    public BudgetProposalResponseDto toBudgetProposalInformationResponseDto(BudgetProposal budgetProposal) {
        var studentOfficer = budgetProposal.getStudentOfficer();
        var moderator = budgetProposal.getModerator();

        return new BudgetProposalResponseDto(
                budgetProposal.getId(),
                budgetProposal.getNameOfActivity(),
                budgetProposal.getDate(),
                budgetProposal.getVenue(),
                budgetProposal.getSourceOfFund(),
                budgetProposal.getAmountAllotted(),
                budgetProposal.getStatus(),
                budgetProposal.getCreatedAt() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(budgetProposal.getCreatedAt()) : "N/A",
                budgetProposal.getLastModified() != null ? this.dateTimeFormatterUtil.formatIntoDateTime(budgetProposal.getLastModified()) : "N/A",
                budgetProposal.getStudentOfficerSignature(),
                budgetProposal.getModeratorSignature(),
                studentOfficer != null ? studentOfficer.getFirstName() + " " + studentOfficer.getMiddleName() + " " + studentOfficer.getLastname() : "N/A",
                moderator != null ? moderator.getFirstName() + " " + moderator.getMiddleName() + " " + moderator.getLastname(): "N/A",
                budgetProposal.getExpectedExpenses()
        );
    }

    public List<BudgetProposalResponseDto> toBudgetProposalInformationResponseDtoList(
            List<BudgetProposal> budgetProposals
    ) {
        return budgetProposals
                .stream()
                .map(this::toBudgetProposalInformationResponseDto)
                .toList();
    }
}

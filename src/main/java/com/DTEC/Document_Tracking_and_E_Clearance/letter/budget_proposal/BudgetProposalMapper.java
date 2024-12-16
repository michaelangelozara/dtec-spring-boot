package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetProposalMapper {

    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public BudgetProposalMapper(DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    public BudgetProposalResponseDto toBudgetProposalInformationResponseDto(BudgetProposal budgetProposal) {
        var studentOfficer = budgetProposal.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.STUDENT_OFFICER)).findFirst();
        var moderator = budgetProposal.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.MODERATOR)).findFirst();
        var dsa = budgetProposal.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.DSA)).findFirst();
        var finance = budgetProposal.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.FINANCE)).findFirst();
        var president = budgetProposal.getSignedPeople().stream().filter(s -> s.getRole().equals(Role.PRESIDENT)).findFirst();

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
                studentOfficer.isPresent() ? studentOfficer.get().getSignature() : "N/A",
                moderator.isPresent() ? moderator.get().getSignature() : "N/A",
                studentOfficer.isPresent() ? studentOfficer.get().getUser().getFirstName() + " " + studentOfficer.get().getUser().getMiddleName() + " " + studentOfficer.get().getUser().getLastname() : "N/A",
                moderator.isPresent() ? moderator.get().getUser().getFirstName() + " " + moderator.get().getUser().getMiddleName() + " " + moderator.get().getUser().getLastname(): "N/A",
                budgetProposal.getExpectedExpenses(),
                budgetProposal.getCurrentLocation(),
                dsa.isPresent() ? dsa.get().getSignature() : "N/A",
                finance.isPresent() ? finance.get().getSignature() :"N/A",
                president.isPresent() ? president.get().getSignature() : "N/A"
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

package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity.ExpectedExpense;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity.ExpectedExpenseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetProposalService {

    private final BudgetProposalRepository budgetProposalRepository;
    private final ExpectedExpenseRepository expectedExpenseRepository;
    private final BudgetProposalMapper budgetProposalMapper;
    private final MemberRoleUtil memberRoleUtil;
    private final UserUtil userUtil;
    private final SignedPeopleRepository signedPeopleRepository;


    public BudgetProposalService(BudgetProposalRepository budgetProposalRepository, ExpectedExpenseRepository expectedExpenseRepository, BudgetProposalMapper budgetProposalMapper, MemberRoleUtil memberRoleUtil, UserUtil userUtil, SignedPeopleRepository signedPeopleRepository) {
        this.budgetProposalRepository = budgetProposalRepository;
        this.expectedExpenseRepository = expectedExpenseRepository;
        this.budgetProposalMapper = budgetProposalMapper;
        this.memberRoleUtil = memberRoleUtil;
        this.userUtil = userUtil;
        this.signedPeopleRepository = signedPeopleRepository;
    }

    @Transactional
    public void proposeBudget(BudgetProposalRequestDto dto) {
        // check if the fields are completely filled out
        if (!areFieldsComplete(dto)) throw new BadRequestException("Please make sure fill all the Blanks out");

        var user = userUtil.getCurrentUser();
        if (user == null)
            throw new ResourceNotFoundException("Student Officer not found");

        var userClub = this.memberRoleUtil.getClubOfOfficer(user.getMemberRoles());

        if(userClub == null) throw new ForbiddenException("You're not Officer in any Club");

        var budgetProposal = BudgetProposal.builder()
                .nameOfActivity(dto.name())
                .date(dto.date())
                .venue(dto.venue())
                .club(userClub)
                .currentLocation(CurrentLocation.MODERATOR)
                .status(LetterStatus.FOR_EVALUATION)
                .type(TypeOfLetter.BUDGET_PROPOSAL)
                .sourceOfFund(dto.sourceOfFund())
                .amountAllotted(dto.allottedAmount())
                .build();

        var savedBudgetProposal = this.budgetProposalRepository.save(budgetProposal);

        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(dto.studentOfficerSignature())
                .budgetProposal(savedBudgetProposal)
                .build();

        List<ExpectedExpense> expectedExpenses = new ArrayList<>();
        for (var expectedExpense : dto.expectedExpenses()) {
            var tempExpectedExpense = ExpectedExpense.builder()
                    .name(expectedExpense.name())
                    .amount(expectedExpense.amount())
                    .budgetProposal(savedBudgetProposal)
                    .build();
            expectedExpenses.add(tempExpectedExpense);
        }

        this.signedPeopleRepository.save(signedPeople);
        this.expectedExpenseRepository.saveAll(expectedExpenses);
    }

    private boolean areFieldsComplete(BudgetProposalRequestDto dto) {
        if (dto.name().isEmpty()) return false;
        if (dto.date() == null) return false;
        if (dto.venue().isEmpty()) return false;
        if (dto.sourceOfFund().isEmpty()) return false;
        if (dto.allottedAmount() == null) return false;
        if (dto.expectedExpenses().isEmpty()) return false;
        if (dto.studentOfficerSignature().isEmpty()) return false;

        return true;
    }

    public List<BudgetProposalResponseDto> getAllBudgetProposal(int s, int e){
        Pageable pageable = PageRequest.of(s, e);
        Page<BudgetProposal> budgetProposals = this.budgetProposalRepository.findAll(pageable);
        return this.budgetProposalMapper.toBudgetProposalInformationResponseDtoList(budgetProposals.getContent());
    }

    public BudgetProposalResponseDto getBudgetProposal(int id){
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget Proposal not Found"));

        return this.budgetProposalMapper.toBudgetProposalInformationResponseDto(budgetProposal);
    }
}
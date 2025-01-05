package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.api_response.ApiResponse;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budget-proposals")
@EnableMethodSecurity
public class BudgetProposalController {

    private final BudgetProposalService budgetProposalService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;


    public BudgetProposalController(BudgetProposalService budgetProposalService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.budgetProposalService = budgetProposalService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }

    @PostMapping("/propose-budget")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR', 'STUDENT_OFFICER')")
    public ResponseEntity<ApiResponse<Void>> proposeBudget(
            @RequestBody BudgetProposalRequestDto dto
    ) {
        this.budgetProposalService.proposeBudget(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Budget Proposal has been saved, Please contact your Moderator for final submission",
                        null,
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponse<List<BudgetProposalResponseDto>>> getAllBudgetProposals(
            @RequestParam(name = "s", defaultValue = "0") int s,
            @RequestParam(name = "e", defaultValue = "10") int e
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "Successfully Fetched Budget Proposals",
                        this.budgetProposalService.getAllBudgetProposal(s, e),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime()
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STUDENT_OFFICER', 'MODERATOR', 'DSA', 'PRESIDENT', 'FINANCE', 'OFFICE_HEAD')")
    public ResponseEntity<ApiResponse<BudgetProposalResponseDto>> getBudgetProposal(
            @PathVariable("id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(
                        true,
                        "",
                        this.budgetProposalService.getBudgetProposal(id),
                        "",
                        this.dateTimeFormatterUtil.formatIntoDateTime())
        );
    }
}

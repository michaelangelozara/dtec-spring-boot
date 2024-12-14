package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetProposalRepository extends JpaRepository<BudgetProposal, Integer> {

    @Query("SELECT b FROM BudgetProposal b WHERE b.status =:status")
    Page<BudgetProposal> findAll(@Param("status") LetterStatus status, Pageable pageable);
}

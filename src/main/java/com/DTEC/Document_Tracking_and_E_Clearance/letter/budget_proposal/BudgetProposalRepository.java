package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetProposalRepository extends JpaRepository<BudgetProposal, Integer> {

    @Query("SELECT b FROM BudgetProposal b WHERE STR(b.currentLocation) =:currentLocation")
    Page<BudgetProposal> findAll(@Param("currentLocation") String currentLocation, Pageable pageable);

    @Query("SELECT b FROM BudgetProposal b JOIN b.club c JOIN c.memberRoles mr WHERE mr.user.id =:userID")
    Page<BudgetProposal> findAll(Pageable pageable, @Param("userID") int id);
}

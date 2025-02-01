package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "expected_expenses")
@AllArgsConstructor
public class ExpectedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_proposal_id")
    @JsonBackReference
    private BudgetProposal budgetProposal;
}

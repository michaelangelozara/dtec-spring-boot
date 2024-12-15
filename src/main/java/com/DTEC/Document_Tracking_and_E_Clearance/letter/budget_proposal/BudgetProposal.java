package com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.SharedFields;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.sub_entity.ExpectedExpense;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "budget_proposals")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BudgetProposal implements SharedFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name_of_activity", nullable = false)
    private String nameOfActivity;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String venue;

    @Column(nullable = false, name = "source_of_fund")
    private String sourceOfFund;

    @Column(precision = 10, scale = 2, name = "amount_allotted")
    private BigDecimal amountAllotted;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    @Enumerated(EnumType.STRING)
    private TypeOfLetter type;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;

    @OneToMany(mappedBy = "budgetProposal")
    @JsonManagedReference
    private List<ExpectedExpense> expectedExpenses;

    @OneToMany(mappedBy = "budgetProposal")
    @JsonManagedReference
    private List<SignedPeople> signedPeople;

    @Override
    public String getNameOfTransaction() {
        return this.nameOfActivity;
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "people_signed")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SignedPeople {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private SignedPeopleStatus status;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String signature;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "budget_proposal_id")
    @JsonBackReference
    private BudgetProposal budgetProposal;

    @ManyToOne
    @JoinColumn(name = "communication_id")
    @JsonBackReference
    private CommunicationLetter communicationLetter;

    @ManyToOne
    @JoinColumn(name = "implementation_letter_in_campus_id")
    @JsonBackReference
    private ImplementationLetterInCampus implementationLetterInCampus;

    @ManyToOne
    @JoinColumn(name = "implementation_letter_off_campus_id")
    @JsonBackReference
    private ImplementationLetterOffCampus implementationLetterOffCampus;
}

package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.SharedFields;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "implementation_letter_in_campuses")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ImplementationLetterInCampus implements SharedFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name_of_activity", nullable = false)
    private String nameOfActivity;

    @Column(name = "semester_and_school_year", nullable = false)
    private String semesterAndSchoolYear;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Size(max = 100, message = "Venue must not exceed 100 Characters")
    private String venue;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "expected_output", nullable = false)
    private String expectedOutput;

    @Column(nullable = false)
    private String objective;

    @Column(name = "projected_expense", nullable = false)
    private String projectedExpense;

    @Column(name = "source_of_fund", nullable = false)
    private String sourceOfFund;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified;

    @Column(nullable = false)
    private String participants;

    @Column(nullable = false)
    @Size(max = 1000, message = "Rationale must not exceed 1000 Characters")
    private String rationale;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    @Enumerated(EnumType.STRING)
    private TypeOfLetter type;

    @OneToMany(mappedBy = "implementationLetterInCampus")
    @JsonManagedReference
    private List<SignedPeople> signedPeople;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Override
    public String getNameOfTransaction() {
        return this.nameOfActivity;
    }
}

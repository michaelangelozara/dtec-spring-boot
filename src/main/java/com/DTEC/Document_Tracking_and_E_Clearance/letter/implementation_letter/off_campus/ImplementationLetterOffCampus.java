package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.SharedFields;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
@Table(name = "implementation_letter_out_campuses")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ImplementationLetterOffCampus implements SharedFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title_of_activity", nullable = false)
    private String titleOfActivity;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String reasons;

    @Column(name = "date_and_time_of_implementation", nullable = false)
    private LocalDateTime dateAndTimeOfImplementation;

    @Column(name = "program_or_flow", nullable = false)
    private String programOrFlow;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "reason_of_rejection", columnDefinition = "VARCHAR(2000)")
    private String reasonOfRejection;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_location", nullable = false)
    private CurrentLocation currentLocation;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    @Enumerated(EnumType.STRING)
    private TypeOfLetter type;

    @OneToMany(mappedBy = "implementationLetterOffCampus")
    @JsonManagedReference
    private List<CAOO> caoos;

    @OneToMany(mappedBy = "implementationLetterOffCampus")
    @JsonManagedReference
    private List<SignedPeople> signedPeople;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;

    @Override
    public String getNameOfTransaction() {
        return this.titleOfActivity;
    }
}

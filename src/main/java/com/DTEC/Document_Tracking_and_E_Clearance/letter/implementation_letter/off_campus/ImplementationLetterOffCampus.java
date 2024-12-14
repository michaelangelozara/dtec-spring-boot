package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.SharedFields;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOO;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
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

    @Lob
    @Column(name = "student_officer_signature", columnDefinition = "LONGTEXT", nullable = false)
    private String studentOfficerSignature;

    @Lob
    @Column(name = "moderator_signature", columnDefinition = "LONGTEXT")
    private String moderatorSignature;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    @Enumerated(EnumType.STRING)
    private TypeOfLetter type;

    @OneToMany(mappedBy = "implementationLetterOffCampus")
    @JsonManagedReference
    private List<CAOO> caoos;

    @ManyToOne
    @JoinColumn(name = "student_officer_id")
    @JsonBackReference
    private User studentOfficer;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    @JsonBackReference
    private User moderator;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;

    @Override
    public String getNameOfTransaction() {
        return this.titleOfActivity;
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;


import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.SharedFields;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "communication_letter")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CommunicationLetter implements SharedFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "VARCHAR(2000)", nullable = false)
    private String letterOfContent;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    @Lob
    @Column(name = "student_officer_signature", nullable = false, columnDefinition = "LONGTEXT")
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
    private CommunicationLetterType typeOfCampus;

    @Enumerated(EnumType.STRING)
    private TypeOfLetter type;

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
        return type.name();
    }
}

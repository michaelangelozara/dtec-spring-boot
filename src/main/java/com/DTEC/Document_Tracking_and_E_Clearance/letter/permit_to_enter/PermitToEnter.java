package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.SharedFields;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "permit_to_enters")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PermitToEnter implements SharedFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private LetterStatus status;

    @Enumerated(EnumType.STRING)
    private TypeOfLetter type;

    @Enumerated(EnumType.STRING)
    private CurrentLocation currentLocation;

    @Column(nullable = false)
    private String activity;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_from")
    private String timeFrom;

    @Column(name = "time_to")
    private String timeTo;

    @Column(nullable = false, columnDefinition = "VARCHAR(2000)")
    private String participants;

    @Column(name = "reason_of_rejection", columnDefinition = "VARCHAR(2000)")
    private String reasonOfRejection;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "permitToEnter")
    @JsonManagedReference
    private List<SignedPeople> signedPeople;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;

    @Override
    public String getNameOfTransaction() {
        return this.activity;
    }
}

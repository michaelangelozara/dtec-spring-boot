package com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "implementation_letters")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ImplementationLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "club_name", nullable = false)
    private String clubName;

    @Column(name = "name_of_activity", nullable = false)
    private String nameOfActivity;

    @Column(name = "semester_and_school_year", nullable = false)
    private String semesterAndSchoolYear;

    @Column(nullable = false)
    private String title;

    @Column(name = "date_time", nullable = false)
    private String dateTime;

    @Column(nullable = false)
    @Size(max = 100, message = "Venue must not exceed 100 Characters")
    private String venue;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @Column(nullable = false)
    private String participants;

    @Column(nullable = false)
    @Size(max = 1000, message = "Rationale must not exceed 1000 Characters")
    private String rationale;

    @Column(nullable = false)
    @Size(max = 1500, message = "Objective must not exceed 1500 Characters")
    private String objectives;

    @Column(name = "source_of_fund", nullable = false)
    private String sourceOfFund;

    @Column(name = "projected_expenses", nullable = false)
    private String projectedExpenses;

    @Column(name = "expected_output", nullable = false)
    private String expectedOutput;

    @Lob
    @Column(nullable = false)
    private String signature;

    @ManyToOne
    @JoinColumn(name = "mayor_id")
    private User mayor;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @JsonBackReference
    private Club club;
}

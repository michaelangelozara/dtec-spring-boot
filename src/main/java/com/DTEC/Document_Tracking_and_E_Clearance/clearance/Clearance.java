package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "clearances")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Clearance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "School Year cannot be Blank")
    @Column(name = "school_year", nullable = false)
    private String schoolYear;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @Lob
    @Column(name = "student_signature", columnDefinition = "LONGTEXT")
    private String studentSignature;

    @Column(name = "is_submitted")
    private boolean isSubmitted;

    @Column(name = "is_clearance_permit_released")
    private boolean isClearancePermitReleased;

    @Enumerated(EnumType.STRING)
    private ClearanceType type;

    @Lob
    @Column(name = "date_of_student_signature")
    private LocalDate dateOfStudentSignature;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private User user;

    @Enumerated(EnumType.STRING)
    private ClearanceStatus status;

    @OneToMany(mappedBy = "clearance", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<ClearanceSignoff> clearanceSignoffs;
}

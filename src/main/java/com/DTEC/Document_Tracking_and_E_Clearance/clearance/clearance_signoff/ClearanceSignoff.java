package com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.Clearance;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
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
@ToString
@Entity
@Table(name = "clearance_signoffs")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ClearanceSignoff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @Column(name = "date_and_time_of_signature")
    private LocalDateTime dateAndTimeOfSignature;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String signature;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "VARCHAR(2000)")
    private String note;

    @Enumerated(EnumType.STRING)
    private ClearanceSignOffStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "clearance_id")
    @JsonBackReference
    private Clearance clearance;
}

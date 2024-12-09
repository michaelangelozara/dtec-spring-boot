package com.DTEC.Document_Tracking_and_E_Clearance.clearance_signoff;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.Clearance;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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

    @Lob
    private String signature;

    @Enumerated(EnumType.STRING)
    private TypeOfSign type;

    @ManyToOne
    @JoinColumn(name = "personnel_id")
    private User personnel;

    @ManyToOne
    @JoinColumn(name = "clearance_id")
    @JsonBackReference
    private Clearance clearance;
}

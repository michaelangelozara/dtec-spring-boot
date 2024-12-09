package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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
@ToString
@Entity
@Table(name = "institutional_outreaches")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IOPP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Size(max = 1500, message = "The Rationale must not exceed 1500 Characters")
    @Column(nullable = false)
    private String rationale;

    @Size(max = 1500, message = "The Target Group/Reason must not exceed 1500 Characters")
    @Column(nullable = false, name = "target_group_and_reason")
    private String targetGroup;

    @Size(max = 1500, message = "The Date and Place must not exceed 1500 Characters")
    @Column(nullable = false, name = "date_and_place")
    private String dateAndPlace;

    @Size(max = 1500, message = "The Program or Flow of Activities must not exceed 1500 Characters")
    @Column(name = "program_or_flow_of_activity", nullable = false)
    private String programOrFlowOfActivity;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @OneToMany(mappedBy = "iopp")
    @JsonManagedReference
    private List<CAOO> caoos;
}

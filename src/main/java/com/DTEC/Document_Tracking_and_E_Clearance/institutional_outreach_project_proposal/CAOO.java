package com.DTEC.Document_Tracking_and_E_Clearance.institutional_outreach_project_proposal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "caoo")
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CAOO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 1500, message = "Activity must not exceed 1500 Characters")
    @Column(nullable = false)
    private String activities;

    @Size(max = 1500, message = "Objectives must not exceed 1500 Characters")
    @Column(nullable = false)
    private String objectives;

    @Size(max = 1500, message = "Expected Output must not exceed 1500 Characters")
    @Column(nullable = false, name = "expected_output")
    private String expectedOutput;

    @Size(max = 100, message = "Committee In-Charge must not exceed 100 Characters")
    @Column(nullable = false)
    private String committeeInCharges;

    @ManyToOne
    @JoinColumn(name = "iopp_id")
    private IOPP iopp;
}

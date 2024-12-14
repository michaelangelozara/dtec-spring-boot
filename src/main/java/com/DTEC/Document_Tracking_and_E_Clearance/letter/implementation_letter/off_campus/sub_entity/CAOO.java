package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "caoo")
@AllArgsConstructor
public class CAOO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String activity;

    @Column(nullable = false)
    private String objective;

    @Column(name = "expected_output", nullable = false)
    private String expectedOutput;

    @Column(nullable = false)
    private String committee;

    @ManyToOne
    @JoinColumn(name = "implementation_letter_out_campus_id")
    @JsonBackReference
    private ImplementationLetterOffCampus implementationLetterOffCampus;
}

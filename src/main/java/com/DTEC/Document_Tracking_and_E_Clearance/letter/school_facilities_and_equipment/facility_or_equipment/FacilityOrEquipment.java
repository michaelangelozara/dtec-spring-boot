package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment.facility_or_equipment;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment.SFEF;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "facility_or_equipments")
@AllArgsConstructor
public class FacilityOrEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "sfef_id")
    private SFEF sfef;
}

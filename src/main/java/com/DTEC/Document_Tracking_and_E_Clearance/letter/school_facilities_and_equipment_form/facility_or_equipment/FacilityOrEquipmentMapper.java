package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form.facility_or_equipment;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacilityOrEquipmentMapper {

    public FacilityOrEquipmentResponseDto toFacilityOrEquipmentResponseDto(FacilityOrEquipment facilityOrEquipment){
        return new FacilityOrEquipmentResponseDto(
                facilityOrEquipment.getId(),
                facilityOrEquipment.getName(),
                facilityOrEquipment.getQuantity()
        );
    }

    public List<FacilityOrEquipmentResponseDto> toFacilityOrEquipmentResponseDtoList(List<FacilityOrEquipment> facilityOrEquipments){
        return facilityOrEquipments
                .stream()
                .map(this::toFacilityOrEquipmentResponseDto)
                .toList();
    }
}

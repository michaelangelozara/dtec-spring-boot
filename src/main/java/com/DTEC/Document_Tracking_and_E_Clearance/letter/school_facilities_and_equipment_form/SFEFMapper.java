package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form.facility_or_equipment.FacilityOrEquipmentMapper;
import org.springframework.stereotype.Service;

@Service
public class SFEFMapper {

    private final FacilityOrEquipmentMapper facilityOrEquipmentMapper;

    public SFEFMapper(FacilityOrEquipmentMapper facilityOrEquipmentMapper) {
        this.facilityOrEquipmentMapper = facilityOrEquipmentMapper;
    }

    public SFEF toSFEF(SFEFRequestDto dto){
        return SFEF.builder()
                .status(LetterStatus.FOR_EVALUATION)
                .type(TypeOfLetter.SFEF)
                .currentLocation(CurrentLocation.MODERATOR)
                .venue(dto.venue())
                .activity(dto.activity())
                .date(dto.date())
                .timeFrom(dto.timeFrom())
                .timeTo(dto.timeTo())
                .build();
    }

    public SFEFResponseDto toSFEFResponseDto(SFEF sfef){
        return new SFEFResponseDto(
                sfef.getId(),
                sfef.getVenue(),
                sfef.getActivity(),
                sfef.getDate(),
                sfef.getTimeFrom(),
                sfef.getTimeTo(),
                this.facilityOrEquipmentMapper.toFacilityOrEquipmentResponseDtoList(sfef.getFacilityOrEquipments())
        );
    }
}

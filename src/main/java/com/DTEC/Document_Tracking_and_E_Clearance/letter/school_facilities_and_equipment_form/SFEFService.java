package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form;

public interface SFEFService {

    void add(SFEFRequestDto dto);

    SFEFResponseDto getSFEFById(int id);
}

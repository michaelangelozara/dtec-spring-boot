package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment.facility_or_equipment.FacilityOrEquipmentMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SFEFMapper {

    private final FacilityOrEquipmentMapper facilityOrEquipmentMapper;
    private final SignedPeopleMapper signedPeopleMapper;
    private final MemberRoleUtil memberRoleUtil;

    public SFEFMapper(FacilityOrEquipmentMapper facilityOrEquipmentMapper, SignedPeopleMapper signedPeopleMapper, MemberRoleUtil memberRoleUtil) {
        this.facilityOrEquipmentMapper = facilityOrEquipmentMapper;
        this.signedPeopleMapper = signedPeopleMapper;
        this.memberRoleUtil = memberRoleUtil;
    }

    public SFEF toSFEF(SFEFRequestDto dto) {
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

    public SFEFResponseDto toSFEFResponseDto(SFEF sfef) {
        var studentOfficer = sfef.getSignedPeople().isEmpty() ? null :
                sfef.getSignedPeople().stream().filter(sp -> sp.getUser() != null && sp.getUser().getRole().equals(Role.STUDENT_OFFICER))
                        .findFirst().orElse(null);
        return new SFEFResponseDto(
                sfef.getId(),
                sfef.getVenue(),
                sfef.getActivity(),
                sfef.getDate(),
                sfef.getTimeFrom(),
                sfef.getTimeTo(),
                this.facilityOrEquipmentMapper.toFacilityOrEquipmentResponseDtoList(sfef.getFacilityOrEquipments()),
                studentOfficer != null ? UserUtil.getUserFullName(studentOfficer.getUser()) : "",
                studentOfficer != null ? this.memberRoleUtil.getClubOfOfficer(studentOfficer.getUser().getMemberRoles()).getShortName() : "",
                studentOfficer != null ? studentOfficer.getUser().getRole().name() : "",
                !sfef.getSignedPeople().isEmpty() ? this.signedPeopleMapper.toSignedPeopleResponseDtoList(sfef.getSignedPeople()) : List.of()
        );
    }
}

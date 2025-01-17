package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermitToEnterMapper {
    private final MemberRoleUtil memberRoleUtil;
    private final SignedPeopleMapper signedPeopleMapper;

    public PermitToEnterMapper(MemberRoleUtil memberRoleUtil, SignedPeopleMapper signedPeopleMapper) {
        this.memberRoleUtil = memberRoleUtil;
        this.signedPeopleMapper = signedPeopleMapper;
    }

    public PermitToEnter toPermitToEnter(PermitToEnterRequestDto dto) {
        return PermitToEnter.builder()
                .activity(dto.activity())
                .date(dto.date())
                .timeFrom(dto.timeFrom())
                .timeTo(dto.timeTo())
                .type(TypeOfLetter.PERMIT_TO_ENTER)
                .currentLocation(CurrentLocation.MODERATOR)
                .status(LetterStatus.FOR_EVALUATION)
                .participants(dto.participants())
                .build();
    }

    public PermitToEnterResponseDto toPermitToEnterResponseDto(PermitToEnter permitToEnter){
        var studentOfficer = permitToEnter.getSignedPeople().isEmpty() ? null :
                permitToEnter.getSignedPeople().stream().filter(sp -> sp.getUser() != null && sp.getUser().getRole().equals(Role.STUDENT_OFFICER))
                        .findFirst().orElse(null);

        return new PermitToEnterResponseDto(
                permitToEnter.getId(),
                permitToEnter.getActivity(),
                permitToEnter.getDate(),
                permitToEnter.getTimeFrom(),
                permitToEnter.getTimeTo(),
                permitToEnter.getParticipants(),
                studentOfficer != null ? UserUtil.getUserFullName(studentOfficer.getUser()) : "",
                studentOfficer != null ? this.memberRoleUtil.getClubOfOfficer(studentOfficer.getUser().getMemberRoles()).getShortName() : "",
                studentOfficer != null ? studentOfficer.getUser().getRole().name() : "",
                !permitToEnter.getSignedPeople().isEmpty() ? this.signedPeopleMapper.toSignedPeopleResponseDtoList(permitToEnter.getSignedPeople()) : List.of()
        );
    }
}

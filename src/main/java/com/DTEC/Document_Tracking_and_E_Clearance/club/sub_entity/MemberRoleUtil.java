package com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberRoleUtil {

    public Club getClubByStudentOfficer(List<MemberRole> memberRoles){
        Club club = null;
        for(var memberRole : memberRoles){
            if(memberRole.getRole().equals(ClubRole.STUDENT_OFFICER)){
                club = memberRole.getClub();
                break;
            }
        }
        return club;
    }
}

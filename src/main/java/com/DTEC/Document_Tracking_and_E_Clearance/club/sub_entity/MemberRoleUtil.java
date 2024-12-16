package com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberRoleUtil {

    public Club getClubOfOfficer(List<MemberRole> memberRoles){
        Club club = null;
        for(var memberRole : memberRoles){
            if(memberRole.getRole().equals(ClubRole.STUDENT_OFFICER) || memberRole.getRole().equals(ClubRole.MODERATOR)){
                club = memberRole.getClub();
                break;
            }
        }
        return club;
    }

    public Club getClubOfMember(List<MemberRole> memberRoles){
        Club club = null;
        for(var memberRole : memberRoles){
            if(memberRole.getRole().equals(ClubRole.MEMBER)){
                club = memberRole.getClub();
                break;
            }
        }
        return club;
    }
}

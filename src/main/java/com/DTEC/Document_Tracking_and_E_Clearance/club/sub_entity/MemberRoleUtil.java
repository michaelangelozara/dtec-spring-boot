package com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.DTEC.Document_Tracking_and_E_Clearance.club.Type;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
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

    public Club getClubByType(List<MemberRole> memberRoles, Type type){
        return memberRoles
                .stream()
                .filter(mr -> {
                    Club club = mr.getClub();
                    return club != null && club.getType().equals(type);
                })
                .map(MemberRole::getClub)
                .findFirst()
                .orElse(null); // Return null if no match is found
    }

    public ClubRole getClubRoleByClub(Club club, int userId){
        var memberRole = club.getMemberRoles()
                .stream()
                .filter(mr -> mr.getUser() != null && mr.getUser().getId().equals(userId))
                .findFirst();

        return memberRole.map(MemberRole::getRole).orElse(null);
    }
}

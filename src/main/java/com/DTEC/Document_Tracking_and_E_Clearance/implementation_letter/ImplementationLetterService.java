package com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ImplementationLetterService {

    private final ImplementationLetterRepository implementationLetterRepository;
    private final ImplementationLetterMapper implementationLetterMapper;
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    public ImplementationLetterService(ImplementationLetterRepository implementationLetterRepository, ImplementationLetterMapper implementationLetterMapper, UserRepository userRepository, UserUtil userUtil) {
        this.implementationLetterRepository = implementationLetterRepository;
        this.implementationLetterMapper = implementationLetterMapper;
        this.userRepository = userRepository;
        this.userUtil = userUtil;
    }

    @Transactional
    public String addImplementationLetter(ImplementationLetterDto dto) {
        try {
            var mayor = userUtil.getCurrentUser();
            if (mayor == null)
                throw new ResourceNotFoundException("Mayor not found");

            if (!mayor.getRole().equals(Role.STUDENT_OFFICER))
                throw new ForbiddenException("Only Mayor can Perform this Action");

            User moderator = null;
            Club club = null;

            // get the moderator of this club
            boolean isItMayorInDepartmentClub = false;
            boolean isItMayorInSocialClub = false;

            // check if this user is mayor in its department club
            for (var clubAssignedRole : mayor.getCourse().getDepartmentClub().getClubAssignedRoles()) {
                if (clubAssignedRole.getUser().getId().equals(mayor.getId())) {
                    if (clubAssignedRole.getRole().equals(ClubRole.MAYOR)) {

                        boolean hasModerator = false;
                        // get the moderator of this club
                        for (var tempClubAssignedRole : mayor.getCourse().getDepartmentClub().getClubAssignedRoles()) {
                            if (tempClubAssignedRole.getRole().equals(ClubRole.MODERATOR)) {
                                moderator = tempClubAssignedRole.getUser();
                                hasModerator = true;
                                break;
                            }
                        }

                        if (!hasModerator)
                            throw new ForbiddenException("This Club has no Moderator. Please Contact the Admin");

                        club = clubAssignedRole.getClub();
                        isItMayorInDepartmentClub = true;
                        break;
                    }
                }
            }

            // check if this user is mayor in its social club
            for (var clubAssignedRole : mayor.getSocialClub().getClubAssignedRoles()) {
                if (clubAssignedRole.getUser().getId().equals(mayor.getId())) {
                    if (clubAssignedRole.getRole().equals(ClubRole.MAYOR)) {

                        boolean hasModerator = false;
                        // get the moderator of this club
                        for (var tempClubAssignedRole : mayor.getCourse().getDepartmentClub().getClubAssignedRoles()) {
                            if (tempClubAssignedRole.getRole().equals(ClubRole.MODERATOR)) {
                                moderator = tempClubAssignedRole.getUser();
                                hasModerator = true;
                                break;
                            }
                        }

                        if (!hasModerator)
                            throw new ForbiddenException("This Club has no Moderator. Please Contact the Admin");

                        club = clubAssignedRole.getClub();
                        isItMayorInSocialClub = true;
                        break;
                    }
                }
            }

            if (!isItMayorInDepartmentClub && !isItMayorInSocialClub)
                throw new ForbiddenException("This User is not Mayor in Departmental and Social Club");

            var implementationLetter = this.implementationLetterMapper.toImplementationLetter(dto);
            implementationLetter.setMayor(mayor);
            implementationLetter.setClub(club);
            implementationLetter.setModerator(moderator);
            this.implementationLetterRepository.save(implementationLetter);
            return "Implementation Letter Submitted";
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOO;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOORepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImplementationLetterOffCampusService {

    private final UserUtil userUtil;
    private final ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository;
    private final ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper;
    private final CAOORepository caooRepository;
    private final MemberRoleUtil memberRoleUtil;

    public ImplementationLetterOffCampusService(UserUtil userUtil, ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository, ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper, CAOORepository caooRepository, MemberRoleUtil memberRoleUtil) {
        this.userUtil = userUtil;
        this.implementationLetterOffCampusRepository = implementationLetterOffCampusRepository;
        this.implementationLetterOffCampusMapper = implementationLetterOffCampusMapper;
        this.caooRepository = caooRepository;
        this.memberRoleUtil = memberRoleUtil;
    }

    @Transactional
    public void requestImplementationLetter(ImplementationLetterOffCampusRequestDto dto) {

        if (!areFieldsComplete(dto)) throw new BadRequestException("Please make sure fill all the Blanks out");

        var user = userUtil.getCurrentUser();
        if (user == null)
            throw new ResourceNotFoundException("Student Officer not found");

        if (!user.getRole().equals(Role.STUDENT_OFFICER))
            throw new ForbiddenException("Only Student Officer can Perform this Action");

        var userClub = this.memberRoleUtil.getClubByStudentOfficer(user.getMemberRoles());

        if (userClub == null) throw new ForbiddenException("You're not Officer in any Club");

        var implementationLetter = this.implementationLetterOffCampusMapper.toImplementationLetterOutCampus(dto);
        implementationLetter.setStudentOfficer(user);
        implementationLetter.setClub(userClub);
        var savedImplementationLetter = this.implementationLetterOffCampusRepository.save(implementationLetter);

        List<CAOO> caoos = new ArrayList<>();
        for (var caoo : dto.caoos()) {
            var tempCaoo = CAOO.builder()
                    .activity(caoo.activity())
                    .objective(caoo.objective())
                    .expectedOutput(caoo.expectedOutput())
                    .committee(caoo.committee())
                    .implementationLetterOffCampus(savedImplementationLetter)
                    .build();
            caoos.add(tempCaoo);
        }
        this.caooRepository.saveAll(caoos);
    }

    private boolean areFieldsComplete(ImplementationLetterOffCampusRequestDto dto) {
        if (dto.title().isEmpty()) return false;
        if (dto.description().isEmpty()) return false;
        if (dto.reason().isEmpty()) return false;
        if (dto.dateAndTime().isEmpty()) return false;
        if (dto.programOrFlowOfActivity().isEmpty()) return false;
        if (dto.caoos().isEmpty()) return false;
        if (dto.signature().isEmpty()) return false;

        return true;
    }
}

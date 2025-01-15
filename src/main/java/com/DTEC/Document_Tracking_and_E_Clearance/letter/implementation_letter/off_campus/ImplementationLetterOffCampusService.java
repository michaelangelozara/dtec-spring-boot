package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOO;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.sub_entity.CAOORepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
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
    private final SignedPeopleRepository signedPeopleRepository;

    public ImplementationLetterOffCampusService(UserUtil userUtil, ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository, ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper, CAOORepository caooRepository, MemberRoleUtil memberRoleUtil, SignedPeopleRepository signedPeopleRepository) {
        this.userUtil = userUtil;
        this.implementationLetterOffCampusRepository = implementationLetterOffCampusRepository;
        this.implementationLetterOffCampusMapper = implementationLetterOffCampusMapper;
        this.caooRepository = caooRepository;
        this.memberRoleUtil = memberRoleUtil;
        this.signedPeopleRepository = signedPeopleRepository;
    }

    @Transactional
    public void requestImplementationLetter(ImplementationLetterOffCampusRequestDto dto) {

        if (!areFieldsComplete(dto)) throw new BadRequestException("Please make sure fill all the Blanks out");

        var user = userUtil.getCurrentUser();
        if (user == null)
            throw new ResourceNotFoundException("Student Officer not found");

        if (!user.getRole().equals(Role.STUDENT_OFFICER))
            throw new ForbiddenException("Only Student Officer can Perform this Action");

        var userClub = this.memberRoleUtil.getClubOfOfficer(user.getMemberRoles());

        if (userClub == null) throw new ForbiddenException("You're not Officer in any Club");

        if(!UserUtil.checkESignature(user)) throw new ForbiddenException("Please Contact the Admin to Register your E-Signature");

        var implementationLetter = this.implementationLetterOffCampusMapper.toImplementationLetterOffCampus(dto);
        implementationLetter.setClub(userClub);
        var savedImplementationLetter = this.implementationLetterOffCampusRepository.save(implementationLetter);

        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(user.getESignature())
                .status(SignedPeopleStatus.EVALUATED)
                .implementationLetterOffCampus(savedImplementationLetter)
                .build();

        var officeHead = getSignedPeople(savedImplementationLetter, Role.OFFICE_HEAD);
        var president = getSignedPeople(savedImplementationLetter, Role.PRESIDENT);

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

        this.signedPeopleRepository.saveAll(List.of(signedPeople, officeHead, president));
        this.caooRepository.saveAll(caoos);
    }

    private SignedPeople getSignedPeople(ImplementationLetterOffCampus implementationLetterOffCampus, Role role){
        return SignedPeople.builder()
                .role(role)
                .status(SignedPeopleStatus.FOR_EVALUATION)
                .implementationLetterOffCampus(implementationLetterOffCampus)
                .build();
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

    public ImplementationLetterOffCampusResponseDto getImplementationLetter(int id){
        var implementationLetter = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementation Letter Off Campus not Found"));

        return this.implementationLetterOffCampusMapper.toImplementationLetterOffCampusResponseDto(implementationLetter);
    }
}

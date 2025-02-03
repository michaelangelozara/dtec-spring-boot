package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.GenericLetterServiceImp;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.GenericLetterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.message.MessageService;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImplementationLetterInCampusService {

    private final ImplementationLetterInCampusRepository implementationLetterInCampusRepository;
    private final ImplementationLetterInCampusMapper implementationLetterInCampusMapper;
    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final SchoolYearGenerator schoolYearGenerator;
    private final MemberRoleUtil memberRoleUtil;
    private final SignedPeopleRepository signedPeopleRepository;
    private final MessageService messageService;
    private final GenericLetterServiceImp genericLetterServiceImp;

    public ImplementationLetterInCampusService(ImplementationLetterInCampusRepository implementationLetterInCampusRepository, ImplementationLetterInCampusMapper implementationLetterInCampusMapper, UserRepository userRepository, UserUtil userUtil, SchoolYearGenerator schoolYearGenerator, MemberRoleUtil memberRoleUtil, SignedPeopleRepository signedPeopleRepository, MessageService messageService, GenericLetterServiceImp genericLetterServiceImp) {
        this.implementationLetterInCampusRepository = implementationLetterInCampusRepository;
        this.implementationLetterInCampusMapper = implementationLetterInCampusMapper;
        this.userRepository = userRepository;
        this.userUtil = userUtil;
        this.schoolYearGenerator = schoolYearGenerator;
        this.memberRoleUtil = memberRoleUtil;
        this.signedPeopleRepository = signedPeopleRepository;
        this.messageService = messageService;
        this.genericLetterServiceImp = genericLetterServiceImp;
    }

    @Transactional
    public void requestImplementationLetter(ImplementationLetterInCampusRequestDto dto) {
        // check if the fields are completely filled out
        if (!areFieldsComplete(dto)) throw new BadRequestException("Please make sure fill all the Blanks out");

        var user = userUtil.getCurrentUser();
        if (user == null)
            throw new ResourceNotFoundException("Student Officer not found");

        if (!user.getRole().equals(Role.STUDENT_OFFICER))
            throw new ForbiddenException("Only Student Officer can Perform this Action");

        var userClub = this.memberRoleUtil.getClubOfOfficer(user.getMemberRoles());

        if (userClub == null) throw new ForbiddenException("You're not Officer in any Club");

        var implementationLetter = this.implementationLetterInCampusMapper.toImplementationLetter(dto);
        implementationLetter.setClub(userClub);

        if(!UserUtil.checkESignature(user)) throw new ForbiddenException("Please Contact the Admin to Register your E-Signature");

        var savedImplementation = this.implementationLetterInCampusRepository.save(implementationLetter);

        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(user.getESignature())
                .status(SignedPeopleStatus.EVALUATED)
                .implementationLetterInCampus(savedImplementation)
                .build();

        var moderator = getSignedPeople(savedImplementation, Role.MODERATOR);
        var dsa = getSignedPeople(savedImplementation, Role.DSA);

        this.signedPeopleRepository.saveAll(List.of(signedPeople, moderator, dsa));

        // send message
        String fullName = UserUtil.getUserFullName(user);
        String message = GenericLetterUtil.generateMessageWhenLetterIsSubmittedOrMovesToTheNextOffice(fullName, savedImplementation);
        this.messageService.sendMessage(user.getContactNumber(), message);

        // send message to Moderator
        this.genericLetterServiceImp.sendMessageToModerator(user, savedImplementation);
    }

    private SignedPeople getSignedPeople(ImplementationLetterInCampus implementationLetterInCampus, Role role){
        return SignedPeople.builder()
                .role(role)
                .status(SignedPeopleStatus.FOR_EVALUATION)
                .implementationLetterInCampus(implementationLetterInCampus)
                .build();
    }

    private boolean areFieldsComplete(ImplementationLetterInCampusRequestDto dto) {
        if (dto.nameOfActivity().isEmpty()) return false;
        if (dto.venue().isEmpty()) return false;
        if (dto.participants().isEmpty()) return false;
        if (dto.rationale().isEmpty()) return false;
        if (dto.signature().isEmpty()) return false;

        if (dto.dateTimes() == null) return false;
        if (dto.objectives().isEmpty()) return false;
        if (dto.sourceOfFunds().isEmpty()) return false;
        if (dto.projectedExpenses().isEmpty()) return false;
        if (dto.expectedOutputs().isEmpty()) return false;

        return true;
    }

    public ImplementationLetterInCampusResponseDto getImplementationLetter(int id){
        var implementationLetter = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementation Letter In Campus not Found"));

        return this.implementationLetterInCampusMapper.toImplementationLetterInCampusResponseDto(implementationLetter);
    }
}

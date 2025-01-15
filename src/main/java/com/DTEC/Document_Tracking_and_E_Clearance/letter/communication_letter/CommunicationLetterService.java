package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.CurrentLocation;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunicationLetterService {

    private final CommunicationLetterRepository communicationLetterRepository;
    private final CommunicationLetterMapper communicationLetterMapper;
    private final MemberRoleUtil memberRoleUtil;
    private final UserUtil userUtil;
    private final SignedPeopleRepository signedPeopleRepository;

    public CommunicationLetterService(CommunicationLetterRepository communicationLetterRepository, CommunicationLetterMapper communicationLetterMapper, MemberRoleUtil memberRoleUtil, UserUtil userUtil, SignedPeopleRepository signedPeopleRepository) {
        this.communicationLetterRepository = communicationLetterRepository;
        this.communicationLetterMapper = communicationLetterMapper;
        this.memberRoleUtil = memberRoleUtil;
        this.userUtil = userUtil;
        this.signedPeopleRepository = signedPeopleRepository;
    }

    @Transactional
    public void requestLetter(CommunicationLetterRequestDto dto, CommunicationLetterType type) {
        // check if the fields are completely filled out
        if (!areFieldsComplete(dto)) throw new BadRequestException("Please make sure fill all the Blanks out");

        if (!type.equals(CommunicationLetterType.IN_CAMPUS) && !type.equals(CommunicationLetterType.OFF_CAMPUS))
            throw new ForbiddenException("Unexpected Request, Please Contact the Developer");

        var user = userUtil.getCurrentUser();
        if (user == null)
            throw new ResourceNotFoundException("Student Officer not found");

        if (!user.getRole().equals(Role.STUDENT_OFFICER))
            throw new ForbiddenException("Only Student Officer can Perform this Action");

        var userClub = this.memberRoleUtil.getClubOfOfficer(user.getMemberRoles());

        if (userClub == null) throw new ForbiddenException("You're not Officer in any Club");

        var communicationLetter = CommunicationLetter.builder()
                .date(dto.date())
                .letterOfContent(dto.letterOfContent())
                .club(userClub)
                .currentLocation(CurrentLocation.MODERATOR)
                .type(TypeOfLetter.COMMUNICATION_LETTER)
                .status(LetterStatus.FOR_EVALUATION)
                .typeOfCampus(type)
                .build();

        if(!UserUtil.checkESignature(user)) throw new ForbiddenException("Please Contact the Admin to Register your E-Signature");

        var savedCommunicationLetter = this.communicationLetterRepository.save(communicationLetter);

        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(user.getESignature())
                .status(SignedPeopleStatus.EVALUATED)
                .communicationLetter(savedCommunicationLetter)
                .build();

        var moderator = getSignedPeople(savedCommunicationLetter, Role.MODERATOR);
        var dsa = getSignedPeople(savedCommunicationLetter, Role.DSA);

        SignedPeople presidentOfOfficeHead;
        if (type.equals(CommunicationLetterType.IN_CAMPUS)) {
            presidentOfOfficeHead = getSignedPeople(savedCommunicationLetter, Role.PRESIDENT);
        } else {
            presidentOfOfficeHead = getSignedPeople(savedCommunicationLetter, Role.OFFICE_HEAD);
        }

        this.signedPeopleRepository.saveAll(List.of(signedPeople, moderator, dsa, presidentOfOfficeHead));
    }

    private SignedPeople getSignedPeople(CommunicationLetter communicationLetter, Role role) {
        return SignedPeople.builder()
                .role(role)
                .status(SignedPeopleStatus.FOR_EVALUATION)
                .communicationLetter(communicationLetter)
                .build();
    }

    private boolean areFieldsComplete(CommunicationLetterRequestDto dto) {
        if (dto.letterOfContent().isEmpty()) return false;
        if (dto.signature().isEmpty()) return false;
        if (dto.date() == null) return false;

        return true;
    }

    public List<CommunicationLetterResponseDto> getAllCommunicationLetter(int s, int e, CommunicationLetterType type) {
        Pageable pageable = PageRequest.of(s, e);
        Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(type, pageable);

        return this.communicationLetterMapper.toCommunicationLetterResponseDtoList(communicationLetterPage.getContent());
    }

    public CommunicationLetterResponseDto getCommunicationLetter(int id) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        return this.communicationLetterMapper.toCommunicationLetterResponseDto(communicationLetter);
    }
}

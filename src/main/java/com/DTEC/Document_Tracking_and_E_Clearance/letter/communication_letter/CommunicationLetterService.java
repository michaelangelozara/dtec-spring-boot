package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.TypeOfLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
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

    public CommunicationLetterService(CommunicationLetterRepository communicationLetterRepository, CommunicationLetterMapper communicationLetterMapper, MemberRoleUtil memberRoleUtil, UserUtil userUtil) {
        this.communicationLetterRepository = communicationLetterRepository;
        this.communicationLetterMapper = communicationLetterMapper;
        this.memberRoleUtil = memberRoleUtil;
        this.userUtil = userUtil;
    }

    public void requestLetter(CommunicationLetterRequestDto dto, CommunicationLetterType type) {
        // check if the fields are completely filled out
        if (!areFieldsComplete(dto)) throw new BadRequestException("Please make sure fill all the Blanks out");

        if(!type.equals(CommunicationLetterType.IN_CAMPUS) && !type.equals(CommunicationLetterType.OFF_CAMPUS))
            throw new ForbiddenException("Unexpected Request, Please Contact the Developer");

        var user = userUtil.getCurrentUser();
        if (user == null)
            throw new ResourceNotFoundException("Student Officer not found");

        if (!user.getRole().equals(Role.STUDENT_OFFICER))
            throw new ForbiddenException("Only Student Officer can Perform this Action");

        var userClub = this.memberRoleUtil.getClubByStudentOfficer(user.getMemberRoles());

        if(userClub == null) throw new ForbiddenException("You're not Officer in any Club");

        var communicationLetter = CommunicationLetter.builder()
                .date(dto.date())
                .letterOfContent(dto.letterOfContent())
                .studentOfficerSignature(dto.signature())
                .studentOfficer(user)
                .club(userClub)
                .type(TypeOfLetter.COMMUNICATION_LETTER)
                .status(LetterStatus.PENDING)
                .typeOfCampus(type)
                .build();

        this.communicationLetterRepository.save(communicationLetter);
    }

    private boolean areFieldsComplete(CommunicationLetterRequestDto dto) {
        if (dto.letterOfContent().isEmpty()) return false;
        if (dto.signature().isEmpty()) return false;

        return true;
    }

    public List<CommunicationLetterResponseDto> getAllCommunicationLetter(int s, int e, CommunicationLetterType type){
        Pageable pageable = PageRequest.of(s, e);
        Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(type, pageable);

        return this.communicationLetterMapper.toCommunicationLetterResponseDtoList(communicationLetterPage.getContent());
    }
}

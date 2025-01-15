package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermitToEnterServiceImp implements PermitToEnterService {
    private final PermitToEnterRepository permitToEnterRepository;
    private final PermitToEnterMapper permitToEnterMapper;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final SignedPeopleRepository signedPeopleRepository;
    private final MemberRoleUtil memberRoleUtil;

    public PermitToEnterServiceImp(PermitToEnterRepository permitToEnterRepository, PermitToEnterMapper permitToEnterMapper, UserUtil userUtil, UserRepository userRepository, SignedPeopleRepository signedPeopleRepository, MemberRoleUtil memberRoleUtil) {
        this.permitToEnterRepository = permitToEnterRepository;
        this.permitToEnterMapper = permitToEnterMapper;
        this.userUtil = userUtil;
        this.userRepository = userRepository;
        this.signedPeopleRepository = signedPeopleRepository;
        this.memberRoleUtil = memberRoleUtil;
    }

    @Transactional
    @Override
    public void add(PermitToEnterRequestDto dto) {
        var user = this.userUtil.getCurrentUser();
        var student = this.userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        var permitToEnter = this.permitToEnterMapper.toPermitToEnter(dto);
        var club = this.memberRoleUtil.getClubOfOfficer(student.getMemberRoles());
        if (club == null) throw new ResourceNotFoundException("You can't perform here, You're not an Officer");

        if(!UserUtil.checkESignature(student)) throw new ForbiddenException("Please Contact the Admin to Register your E-Signature");

        permitToEnter.setClub(club);
        var savedPermitToEnter = this.permitToEnterRepository.save(permitToEnter);
        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .status(SignedPeopleStatus.EVALUATED)
                .signature(student.getESignature())
                .permitToEnter(savedPermitToEnter)
                .build();

        var moderator = getSignedPeople(permitToEnter, Role.MODERATOR);
        var officeHead = getSignedPeople(permitToEnter, Role.OFFICE_HEAD);
        var president = getSignedPeople(permitToEnter, Role.PRESIDENT);
        this.signedPeopleRepository.saveAll(List.of(signedPeople, moderator, officeHead, president));
    }

    private SignedPeople getSignedPeople(PermitToEnter permitToEnter, Role role) {
        return SignedPeople.builder()
                .role(role)
                .status(SignedPeopleStatus.FOR_EVALUATION)
                .permitToEnter(permitToEnter)
                .build();
    }

    @Override
    public PermitToEnterResponseDto getPermitToEnterById(int id) {
        var permitToEnter = this.permitToEnterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit To Enter not Found"));
        return this.permitToEnterMapper.toPermitToEnterResponseDto(permitToEnter);
    }
}

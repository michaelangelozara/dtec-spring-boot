package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PermitToEnterServiceImp implements PermitToEnterService{
    private final PermitToEnterRepository permitToEnterRepository;
    private final PermitToEnterMapper permitToEnterMapper;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final SignedPeopleRepository signedPeopleRepository;

    public PermitToEnterServiceImp(PermitToEnterRepository permitToEnterRepository, PermitToEnterMapper permitToEnterMapper, UserUtil userUtil, UserRepository userRepository, SignedPeopleRepository signedPeopleRepository) {
        this.permitToEnterRepository = permitToEnterRepository;
        this.permitToEnterMapper = permitToEnterMapper;
        this.userUtil = userUtil;
        this.userRepository = userRepository;
        this.signedPeopleRepository = signedPeopleRepository;
    }

    @Transactional
    @Override
    public void add(PermitToEnterRequestDto dto) {
        var user = this.userUtil.getCurrentUser();
        var student = this.userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        var permitToEnter = this.permitToEnterMapper.toPermitToEnter(dto);

        var savedPermitToEnter = this.permitToEnterRepository.save(permitToEnter);
        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .status(SignedPeopleStatus.EVALUATED)
                .signature(student.getESignature())
                .permitToEnter(savedPermitToEnter)
                .build();
        this.signedPeopleRepository.save(signedPeople);
    }

    @Override
    public PermitToEnterResponseDto getPermitToEnterById(int id) {
        var permitToEnter = this.permitToEnterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit To Enter not Found"));
        return this.permitToEnterMapper.toPermitToEnterResponseDto(permitToEnter);
    }
}

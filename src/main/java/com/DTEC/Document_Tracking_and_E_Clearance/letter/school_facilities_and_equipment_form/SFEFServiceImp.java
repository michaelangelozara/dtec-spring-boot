package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form;

import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRoleUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.BadRequestException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form.facility_or_equipment.FacilityOrEquipment;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form.facility_or_equipment.FacilityOrEquipmentRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SFEFServiceImp implements SFEFService{
    private final SFEFRepository sfefRepository;
    private final SFEFMapper sfefMapper;
    private final FacilityOrEquipmentRepository facilityOrEquipmentRepository;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final SignedPeopleRepository signedPeopleRepository;
    private final MemberRoleUtil memberRoleUtil;

    public SFEFServiceImp(SFEFRepository sfefRepository, SFEFMapper sfefMapper, FacilityOrEquipmentRepository facilityOrEquipmentRepository, UserUtil userUtil, UserRepository userRepository, SignedPeopleRepository signedPeopleRepository, MemberRoleUtil memberRoleUtil) {
        this.sfefRepository = sfefRepository;
        this.sfefMapper = sfefMapper;
        this.facilityOrEquipmentRepository = facilityOrEquipmentRepository;
        this.userUtil = userUtil;
        this.userRepository = userRepository;
        this.signedPeopleRepository = signedPeopleRepository;
        this.memberRoleUtil = memberRoleUtil;
    }

    @Transactional
    @Override
    public void add(SFEFRequestDto dto) {
        if(dto.facilityOrEquipments().isEmpty())
            throw new BadRequestException("Facilities or Equipment cannot be empty");

        var user = this.userUtil.getCurrentUser();
        var student = this.userRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not Found"));

        if(dto.signature() == null || dto.signature().isEmpty()) throw new ForbiddenException("Please Attach your E-Signature!");

        var sfef = this.sfefMapper.toSFEF(dto);
        var club = this.memberRoleUtil.getClubOfOfficer(student.getMemberRoles());
        if (club == null) throw new ResourceNotFoundException("You can't perform here, You're not an Officer");
        sfef.setClub(club);

        var savedSFEF = this.sfefRepository.save(sfef);

        var signedPeople = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .status(SignedPeopleStatus.EVALUATED)
                .signature(student.getESignature())
                .sfef(savedSFEF)
                .build();

        var moderator = getSignedPeople(savedSFEF, Role.MODERATOR);
        var officeHead = getSignedPeople(savedSFEF, Role.AUXILIARY_SERVICE_HEAD);
        var chapel = getSignedPeople(savedSFEF, Role.CHAPEL);
        var PPIC = getSignedPeople(savedSFEF, Role.PPLO);
        var multimedia = getSignedPeople(savedSFEF, Role.MULTIMEDIA);
        var president = getSignedPeople(savedSFEF, Role.PRESIDENT);

        this.signedPeopleRepository.saveAll(List.of(signedPeople, moderator, officeHead, chapel, PPIC, multimedia, president));

        List<FacilityOrEquipment> facilityOrEquipments = new ArrayList<>();
        for(var facilityOrEquipment : dto.facilityOrEquipments()){
            var tempFacilityOrEquipment = FacilityOrEquipment.builder()
                    .name(facilityOrEquipment.name())
                    .quantity(facilityOrEquipment.quantity())
                    .sfef(savedSFEF)
                    .build();
            facilityOrEquipments.add(tempFacilityOrEquipment);
        }
        this.facilityOrEquipmentRepository.saveAll(facilityOrEquipments);
    }

    private SignedPeople getSignedPeople(SFEF sfef, Role role){
        return SignedPeople.builder()
                .role(role)
                .status(SignedPeopleStatus.FOR_EVALUATION)
                .sfef(sfef)
                .build();
    }

    @Override
    public SFEFResponseDto getSFEFById(int id) {
        var sfef = this.sfefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SFEF not Found"));
        return this.sfefMapper.toSFEFResponseDto(sfef);
    }
}

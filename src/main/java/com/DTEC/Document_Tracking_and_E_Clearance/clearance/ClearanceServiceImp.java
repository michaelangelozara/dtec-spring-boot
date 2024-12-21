package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignOffStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoffRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.DTEC.Document_Tracking_and_E_Clearance.club.Type;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.InternalServerErrorException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ClearanceServiceImp implements ClearanceService {

    private final UserRepository userRepository;
    private final ClearanceSignoffRepository clearanceSignoffRepository;
    private final ClearanceRepository clearanceRepository;
    private final ClearanceMapper clearanceMapper;
    private final SchoolYearGenerator schoolYearGenerator;
    private final UserUtil userUtil;
    private final ClubRepository clubRepository;

    public ClearanceServiceImp(UserRepository userRepository, ClearanceSignoffRepository clearanceSignoffRepository, ClearanceRepository clearanceRepository, ClearanceMapper clearanceMapper, SchoolYearGenerator schoolYearGenerator, UserUtil userUtil, ClubRepository clubRepository) {
        this.userRepository = userRepository;
        this.clearanceSignoffRepository = clearanceSignoffRepository;
        this.clearanceRepository = clearanceRepository;
        this.clearanceMapper = clearanceMapper;
        this.schoolYearGenerator = schoolYearGenerator;
        this.userUtil = userUtil;
        this.clubRepository = clubRepository;
    }

    @Transactional
    @Override
    public String releaseClearances() {
        try {
            var students = this.userRepository.findAllStudents();
            if (students.isEmpty())
                throw new ResourceNotFoundException("No Registered Student yet");

            // fetch all Office in-charges
            var allOfficeInCharge = this.userRepository.findAllOfficeInCharge();

            var dsa = allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(Role.DSA)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the DSA's account is not created yet"));
            var guidance = allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(Role.GUIDANCE)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Guidance's account is not created yet"));
            var cashier = allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(Role.CASHIER)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Cashier's account is not created yet"));
            var librarian = allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(Role.LIBRARIAN)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Librarian's account is not created yet"));
            var schoolNurse = allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(Role.SCHOOL_NURSE)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the School Nurse's account is not created yet"));
            var registrar = allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(Role.REGISTRAR)).findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Registrar's account is not created yet"));

            List<Clearance> clearances = students
                    .stream()
                    .map(student -> Clearance.builder()
                            .schoolYear(this.schoolYearGenerator.generateSchoolYear())
                            .student(student)
                            .status(ClearanceStatus.PENDING)
                            .build()).toList();
            var savedClearances = this.clearanceRepository.saveAll(clearances);

            List<ClearanceSignoff> clearanceSignoffs = new ArrayList<>();
            for (var savedClearance : savedClearances) {
                var cs1 = getClearanceSignoff(dsa, savedClearance);
                var cs2 = getClearanceSignoff(guidance, savedClearance);
                var cs3 = getClearanceSignoff(cashier, savedClearance);
                var cs4 = getClearanceSignoff(librarian, savedClearance);
                var cs5 = getClearanceSignoff(schoolNurse, savedClearance);
                var cs6 = getClearanceSignoff(registrar, savedClearance);

                // get the student of this clearance
                var student = savedClearance.getStudent();

                // get the program head of this student
                var programHead = student.getCourse().getUsers()
                        .stream()
                        .filter(u -> u.getRole().equals(Role.PROGRAM_HEAD))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Program Head's account is not created yet"));
                var cs7 = getClearanceSignoff(programHead, savedClearance);

                // get dean of this student
                var dean = student.getDepartment().getUsers()
                        .stream()
                        .filter(u -> u.getRole().equals(Role.DEAN))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Dean's account is not created yet"));
                ;
                var cs8 = getClearanceSignoff(dean, savedClearance);

                var memberRoleOfDepartmentClub = student.getMemberRoles()
                        .stream()
                        .filter(mr -> mr.getClub().getType().equals(Type.DEPARTMENT)).findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Student's Department Club is not Found"));

                var departmentClub = memberRoleOfDepartmentClub.getClub();

                var moderatorOptional = departmentClub.getMemberRoles()
                        .stream()
                        .filter(mr -> mr.getRole().equals(ClubRole.MODERATOR) && mr.getUser().getRole().equals(Role.MODERATOR))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Student's Club's Moderator is not Found"));

                var cs9 = getClearanceSignoff(moderatorOptional.getUser(), savedClearance);
                clearanceSignoffs.addAll(List.of(
                        cs1,
                        cs2,
                        cs3,
                        cs4,
                        cs5,
                        cs6,
                        cs7,
                        cs8,
                        cs9
                ));
            }
            this.clearanceSignoffRepository.saveAll(clearanceSignoffs);
            return "Clearances Successfully Released";
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    private Club getClubById(int clubId) {
        return this.clubRepository.findById(clubId)
                .orElseThrow(() -> new ResourceNotFoundException("Student Club not Found"));
    }

    private ClearanceSignoff getClearanceSignoff(User user, Clearance clearance) {
        return ClearanceSignoff.builder()
                .status(ClearanceSignOffStatus.PENDING)
                .clearance(clearance)
                .role(user.getRole())
                .user(user)
                .build();
    }

    @Override
    public List<ClearanceResponseDto> getAllClearances(int n) {
        Pageable pageable = PageRequest.of(0, n);
        Page<Clearance> clearances = this.clearanceRepository.findAll(pageable);
        return this.clearanceMapper.toClearanceResponseDtoList(clearances.getContent());
    }

    @Override
    public List<ClearanceResponseDto> getAllStudentClearances() {
        var student = this.userUtil.getCurrentUser();
        if (student == null) throw new UnauthorizedException("Session Expired");

        Pageable pageable = PageRequest.of(0, 50);
        Page<Clearance> clearanceOptional = this.clearanceRepository
                .findAllByStudentId(pageable, student.getId());

        List<Clearance> sortedClearances = clearanceOptional.getContent()
                .stream()
                .sorted(Comparator.comparing(Clearance::getId).reversed())
                .toList();

        return this.clearanceMapper.toClearanceResponseDtoList(sortedClearances);
    }

    @Override
    public String signClearance(int clearanceId, String signature) {
        var clearance = this.clearanceRepository.findById(clearanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance not Found"));

        if (clearance.getStatus().equals(ClearanceStatus.COMPLETED))
            throw new ForbiddenException("This Clearance is not Modifiable any more");

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        if (isSectionSignedAlready(clearance.getClearanceSignoffs(), user.getRole()))
            throw new ForbiddenException("This Section has already been Signed");

        if (user.getRole().equals(Role.DSA)) {
            if (isClearanceReadyForDSAOrDean(clearance.getClearanceSignoffs(), List.of(
                    // required signed roles to proceed to dsa
                    Role.GUIDANCE,
                    Role.LIBRARIAN,
                    Role.SCHOOL_NURSE,
                    Role.REGISTRAR
            ))) {
            } else {
                throw new ForbiddenException("DSA can't sign yet because some required personnel and offices have not signed yet");
            }
        } else if (user.getRole().equals(Role.DEAN)) {
            if (isClearanceReadyForDSAOrDean(clearance.getClearanceSignoffs(), List.of(
                    // required signed roles to proceed to dean
                    Role.GUIDANCE,
                    Role.LIBRARIAN,
                    Role.SCHOOL_NURSE,
                    Role.REGISTRAR,
                    Role.DSA
            ))) {
            } else {
                throw new ForbiddenException("DEAN can't sign yet because some required personnel and offices have not signed yet");
            }
        }

        var clearanceSignoff = clearance.getClearanceSignoffs()
                .stream()
                .filter(cs -> cs.getUser().getId().equals(user.getId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Invalid Office In-Charge Assigned for this Clearance"));

        clearanceSignoff.setSignature(signature);
        clearanceSignoff.setStatus(ClearanceSignOffStatus.COMPLETED);

        this.clearanceSignoffRepository.save(clearanceSignoff);
        return "Signature has been attached";
    }

    @Override
    public ClearanceResponseDto getNewClearance() {
        var student = this.userUtil.getCurrentUser();
        if(student == null) throw new UnauthorizedException("Session Expired");

        Pageable pageable = PageRequest.of(0, 1);
        var clearance = this.clearanceRepository.findNewClearance(pageable).get(0);
        return this.clearanceMapper.toClearanceResponseDto(clearance);
    }

    @Override
    public void onClick(int clearanceId) {
        var user = this.userUtil.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Session Expired");

        var clearance = this.clearanceRepository.findById(clearanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance not Found"));

        var clearanceSignoff = clearance.getClearanceSignoffs()
                .stream()
                .filter(cs -> cs.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Office In-Charge Assigned for this Clearance"));

        if(clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) return;

        clearanceSignoff.setStatus(ClearanceSignOffStatus.IN_PROGRESS);
        this.clearanceSignoffRepository.save(clearanceSignoff);
    }

    private boolean isSectionSignedAlready(List<ClearanceSignoff> clearanceSignoffs, Role role) {
        for (var clearanceSignoff : clearanceSignoffs) {
            if (clearanceSignoff.getRole().equals(role) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) {
                return true;
            }
        }
        return false;
    }

    private boolean isClearanceReadyForDSAOrDean(
            List<ClearanceSignoff> clearanceSignoffs,
            List<Role> signedRoles
    ) {
        for (var signedRole : signedRoles) {
            boolean isExist = false;
            for (var clearanceSignoff : clearanceSignoffs) {
                if (clearanceSignoff.getUser() != null && clearanceSignoff.getRole().equals(signedRole)) {
                    isExist = true;
                    break;
                }
            }

            if (!isExist) {
                return false;
            }
        }
        return true;
    }
}

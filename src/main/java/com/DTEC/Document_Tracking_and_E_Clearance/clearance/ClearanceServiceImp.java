package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignOffStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoffRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.InternalServerErrorException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final CourseRepository courseRepository;

    public ClearanceServiceImp(UserRepository userRepository, ClearanceSignoffRepository clearanceSignoffRepository, ClearanceRepository clearanceRepository, ClearanceMapper clearanceMapper, SchoolYearGenerator schoolYearGenerator, UserUtil userUtil, ClubRepository clubRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.clearanceSignoffRepository = clearanceSignoffRepository;
        this.clearanceRepository = clearanceRepository;
        this.clearanceMapper = clearanceMapper;
        this.schoolYearGenerator = schoolYearGenerator;
        this.userUtil = userUtil;
        this.clubRepository = clubRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    @Override
    public String releaseStudentClearances() {
        try {
            var students = this.userRepository.findAllStudents();
            if (students.isEmpty())
                throw new ResourceNotFoundException("No Registered Student yet");

            // fetch all course
            var courses = this.courseRepository.findAll();

            // fetch all Office in-charges
            var allOfficeInCharge = this.userRepository.findAllOfficeInChargeForStudentClearance();

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
                            .user(student)
                            .type(ClearanceType.STUDENT_CLEARANCE)
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
                // TODO - In this part the assignment of the lab depends on the course of the user

                // get the user of this clearance
                var student = savedClearance.getUser();

                var studentCourse = courses.stream().filter(course -> course.getId().equals(student.getCourse().getId())).findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Course not Found"));

                var labInCharge = ClearanceUtil.getLabInChargeBasedOnStudentCourse(allOfficeInCharge, studentCourse.getShortName());

                var cs9 = getClearanceSignoff(labInCharge, savedClearance);

                // get the program head of this user
                var programHead = student.getCourse().getUsers()
                        .stream()
                        .filter(u -> u.getRole().equals(Role.PROGRAM_HEAD))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Program Head's account is not created yet"));
                var cs7 = getClearanceSignoff(programHead, savedClearance);

                // get dean of this user
                var dean = student.getDepartment().getUsers()
                        .stream()
                        .filter(u -> u.getRole().equals(Role.DEAN))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Dean's account is not created yet"));
                ;
                var cs8 = getClearanceSignoff(dean, savedClearance);

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

    @Override
    public String releasePersonnelClearances() {
        try {
            var students = this.userRepository.findAllPersonnel();
            if (students.isEmpty())
                throw new ResourceNotFoundException("No Registered Personnel yet");

            // fetch all Office in-charges
            var allOfficeInCharge = this.userRepository.findAllOfficeInChargeForPersonnelClearance();

            var multimedia = getUserByRole(allOfficeInCharge, Role.MULTIMEDIA, "Multimedia");
            var librarian = getUserByRole(allOfficeInCharge, Role.LIBRARIAN, "Librarian");
            var cashier = getUserByRole(allOfficeInCharge, Role.CASHIER, "Cashier");
            var registrar = getUserByRole(allOfficeInCharge, Role.REGISTRAR, "Registrar");
            var accountingClerk = getUserByRole(allOfficeInCharge, Role.ACCOUNTING_CLERK, "Accounting Clerk");
            var finance = getUserByRole(allOfficeInCharge, Role.FINANCE, "Finance");
            var custodian = getUserByRole(allOfficeInCharge, Role.CUSTODIAN, "Property Custodian");
            var programHead = getUserByRole(allOfficeInCharge, Role.PROGRAM_HEAD, "Program Head");
            var dean = getUserByRole(allOfficeInCharge, Role.DEAN, "Dean");
            var vpaf = getUserByRole(allOfficeInCharge, Role.VPAF, "VPAF");
            var vpa = getUserByRole(allOfficeInCharge, Role.VPA, "VPA");
            var president = getUserByRole(allOfficeInCharge, Role.PRESIDENT, "President");

            List<Clearance> clearances = students
                    .stream()
                    .map(student -> Clearance.builder()
                            .schoolYear(this.schoolYearGenerator.generateSchoolYear())
                            .user(student)
                            .type(ClearanceType.PERSONNEL_CLEARANCE)
                            .status(ClearanceStatus.PENDING)
                            .build()).toList();
            var savedClearances = this.clearanceRepository.saveAll(clearances);

            List<ClearanceSignoff> clearanceSignoffs = new ArrayList<>();
            for (var savedClearance : savedClearances) {
                var personnel = savedClearance.getUser();
                if (personnel.getType().equals(PersonnelType.ACADEMIC)) {
                    var cs1 = getClearanceSignoff(multimedia, savedClearance);
                    var cs2 = getClearanceSignoff(librarian, savedClearance);
                    var cs3 = getClearanceSignoff(cashier, savedClearance);
                    var cs4 = getClearanceSignoff(registrar, savedClearance);
                    var cs5 = getClearanceSignoff(accountingClerk, savedClearance);
                    var cs6 = getClearanceSignoff(finance, savedClearance);
                    var cs7 = getClearanceSignoff(custodian, savedClearance);
                    var cs8 = getClearanceSignoff(programHead, savedClearance);
                    var cs9 = getClearanceSignoff(dean, savedClearance);
                    var cs10 = getClearanceSignoff(vpaf, savedClearance);
                    var cs11 = getClearanceSignoff(vpa, savedClearance);
                    var cs12 = getClearanceSignoff(president, savedClearance);

                    clearanceSignoffs.addAll(List.of(
                            cs1,
                            cs2,
                            cs3,
                            cs4,
                            cs5,
                            cs6,
                            cs7,
                            cs8,
                            cs9,
                            cs10,
                            cs11,
                            cs12
                    ));
                } else {
                    var cs1 = getClearanceSignoff(multimedia, savedClearance);
                    var cs2 = getClearanceSignoff(librarian, savedClearance);
                    var cs3 = getClearanceSignoff(cashier, savedClearance);
                    var cs4 = getClearanceSignoff(registrar, savedClearance);
                    var cs5 = getClearanceSignoff(accountingClerk, savedClearance);
                    var cs6 = getClearanceSignoff(finance, savedClearance);
                    var cs7 = getClearanceSignoff(custodian, savedClearance);
                    var cs8 = getClearanceSignoff(vpaf, savedClearance);
                    var cs9 = getClearanceSignoff(vpa, savedClearance);
                    var cs10 = getClearanceSignoff(president, savedClearance);

                    clearanceSignoffs.addAll(List.of(
                            cs1,
                            cs2,
                            cs3,
                            cs4,
                            cs5,
                            cs6,
                            cs7,
                            cs8,
                            cs9,
                            cs10
                    ));
                }
            }
            this.clearanceSignoffRepository.saveAll(clearanceSignoffs);
            return "Clearances Successfully Released";
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    private User getUserByRole(List<User> allOfficeInCharge, Role role, String nameOfOffice) {
        return allOfficeInCharge.stream().filter(oic -> oic.getRole().equals(role)).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the " + nameOfOffice + "'s account is not created yet"));
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
    public List<ClearanceResponseDto> getAllClearances() {
        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        List<Clearance> clearanceList = new ArrayList<>();
        // dean, program head
        if (user.getRole().equals(Role.DEAN)) {
            var clearances = this.clearanceRepository.findAllForDean(user.getId(), user.getDepartment().getId());

            // check each clearance if their signatures completed before proceed to the dean
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.GUIDANCE,
                            Role.CASHIER,
                            Role.LIBRARIAN,
                            Role.SCHOOL_NURSE,
                            Role.PROGRAM_HEAD,
                            Role.REGISTRAR,
                            Role.DSA
                    ))) {
                        clearanceList.add(clearance);
                    }
                } else {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD
                    )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                }

            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.PROGRAM_HEAD)) {
            List<Clearance> clearances = this.clearanceRepository.findAllForProgramHead(user.getId());

            // check each clearance if their signatures completed before proceed to the program head
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.GUIDANCE,
                            Role.CASHIER,
                            Role.LIBRARIAN,
                            Role.SCHOOL_NURSE,
                            Role.REGISTRAR
                    )) && ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                } else {
                    clearanceList.add(clearance);
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.DSA)) {
            List<Clearance> clearances = this.clearanceRepository.findAll();
            // check each clearance if their signatures completed before proceed to the program head
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.GUIDANCE,
                            Role.CASHIER,
                            Role.LIBRARIAN,
                            Role.SCHOOL_NURSE,
                            Role.REGISTRAR,
                            Role.PROGRAM_HEAD
                    ))) {
                        clearanceList.add(clearance);
                    }
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.VPAF)) {
            var clearances = this.clearanceRepository.findAllClearancesForVPAFAndVPAAndPresident();
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD,
                            Role.DEAN
                    )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.VPA)) {
            var clearances = this.clearanceRepository.findAllClearancesForVPAFAndVPAAndPresident();
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD,
                            Role.DEAN,
                            Role.VPAF
                    )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            var clearances = this.clearanceRepository.findAllClearancesForVPAFAndVPAAndPresident();
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
                    if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD,
                            Role.DEAN,
                            Role.VPAF,
                            Role.VPA
                    )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (ClearanceUtil.isLabInChargeRole(user.getRole())) {
            List<Clearance> clearances = this.clearanceRepository.findAll();
            for (var clearance : clearances) {
                if (ClearanceUtil.isMyRoleIncludedForSigning(clearance.getClearanceSignoffs(), user.getRole())) {
                    clearanceList.add(clearance);
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else {
            List<Clearance> clearances = this.clearanceRepository.findAll();
            return this.clearanceMapper.toClearanceResponseDtoList(clearances);
        }
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
            if (isClearanceReadyForDSAOrDeanForStudentClearance(clearance.getClearanceSignoffs(), List.of(
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
            if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                if (isClearanceReadyForDSAOrDeanForStudentClearance(clearance.getClearanceSignoffs(), List.of(
                        // required signed roles to proceed to dean
                        Role.GUIDANCE,
                        Role.LIBRARIAN,
                        Role.SCHOOL_NURSE,
                        Role.REGISTRAR,
                        Role.DSA
                )) && ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())) {
                } else {
                    throw new ForbiddenException("DEAN can't sign yet because some required personnel and offices have not signed yet");
                }
            } else {
                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
                        // required signed roles to proceed to dean
                        Role.GUIDANCE,
                        Role.REGISTRAR,
                        Role.CASHIER,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.PROGRAM_HEAD
                )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                    // TODO this is just temporary removed, due to the assigning of type of personnel is not yet implemented
//                    ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())
                } else {
                    throw new ForbiddenException("DEAN can't sign yet because some required Office In-Charge have not signed yet");
                }
            }
        } else if (user.getRole().equals(Role.VPAF)) {
            if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
                        // required signed roles to proceed to dean
                        Role.GUIDANCE,
                        Role.REGISTRAR,
                        Role.CASHIER,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.PROGRAM_HEAD,
                        Role.DEAN
                )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                    // TODO this is just temporary removed, due to the assigning of type of personnel is not yet implemented
//                    ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())
                } else {
                    throw new ForbiddenException("VPAF can't sign yet because some required Office In-Charge have not signed yet");
                }
            }
        } else if (user.getRole().equals(Role.VPA)) {
            if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
                        // required signed roles to proceed to dean
                        Role.GUIDANCE,
                        Role.REGISTRAR,
                        Role.CASHIER,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.PROGRAM_HEAD,
                        Role.VPAF
                )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                    // TODO this is just temporary removed, due to the assigning of type of personnel is not yet implemented
//                    ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())
                } else {
                    throw new ForbiddenException("VPA can't sign yet because some required Office In-Charge have not signed yet");
                }
            }
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
                        // required signed roles to proceed to dean
                        Role.GUIDANCE,
                        Role.REGISTRAR,
                        Role.CASHIER,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.PROGRAM_HEAD,
                        Role.VPAF,
                        Role.VPA
                )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                    // TODO this is just temporary removed, due to the assigning of type of personnel is not yet implemented
//                    ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())
                } else {
                    throw new ForbiddenException("PRESIDENT can't sign yet because some required Office In-Charge have not signed yet");
                }
            }
        }

        var clearanceSignoff = clearance.getClearanceSignoffs()
                .stream()
                .filter(cs -> cs.getUser().getRole().equals(user.getRole()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Invalid Office In-Charge Assigned for this Clearance"));

        clearanceSignoff.setSignature(signature);
        clearanceSignoff.setStatus(ClearanceSignOffStatus.COMPLETED);
        this.clearanceSignoffRepository.save(clearanceSignoff);

        if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
            if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                    Role.DSA,
                    Role.GUIDANCE,
                    Role.CASHIER,
                    Role.LIBRARIAN,
                    Role.SCHOOL_NURSE,
                    Role.REGISTRAR,
                    Role.PROGRAM_HEAD,
                    Role.DEAN
            )) && ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())) {
                clearance.setStatus(ClearanceStatus.COMPLETED);
                this.clearanceRepository.save(clearance);
            }
        } else {
            var personnel = clearance.getUser();
            if (personnel.getType().equals(PersonnelType.ACADEMIC)) {
                if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                        Role.CASHIER,
                        Role.REGISTRAR,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.PROGRAM_HEAD,
                        Role.DEAN,
                        Role.VPAF,
                        Role.VPA,
                        Role.PRESIDENT
                )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                    clearance.setStatus(ClearanceStatus.COMPLETED);
                    this.clearanceRepository.save(clearance);
                }
            } else {
                if (areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                        Role.CASHIER,
                        Role.REGISTRAR,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.VPAF,
                        Role.VPA,
                        Role.PRESIDENT
                )) && ClearanceUtil.areLibrarianAndMultimediaSigned(clearance.getClearanceSignoffs())) {
                    clearance.setStatus(ClearanceStatus.COMPLETED);
                    this.clearanceRepository.save(clearance);
                }
            }

        }

        return "Signature has been attached";
    }

    private boolean areAllSignaturesSettled(List<ClearanceSignoff> clearanceSignoffs, List<Role> roles) {
        for (var role : roles) {
            for (var clearanceSignoff : clearanceSignoffs) {
                if (role.equals(clearanceSignoff.getRole()) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.PENDING) ||
                        role.equals(clearanceSignoff.getRole()) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.IN_PROGRESS))
                    return false;
            }
        }
        return true;
    }

    @Override
    public ClearanceResponseDto getNewClearance() {
        try {
            var student = this.userUtil.getCurrentUser();
            if (student == null) throw new UnauthorizedException("Session Expired");

            var clearancePage = this.clearanceRepository.findClearanceByUserId(student.getId());
            return this.clearanceMapper.toClearanceResponseDto(clearancePage.get(0));
        } catch (IndexOutOfBoundsException e) {
            throw new ForbiddenException("Clearance is not Available, Please Contact the Admin to Release the Clearance");
        }
    }

    @Override
    public void onClick(int clearanceId) {
        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var clearance = this.clearanceRepository.findById(clearanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance not Found"));

        var clearanceSignoff = clearance.getClearanceSignoffs()
                .stream()
                .filter(cs -> cs.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Office In-Charge Assigned for this Clearance"));

        if (clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) return;

        clearanceSignoff.setStatus(ClearanceSignOffStatus.IN_PROGRESS);
        this.clearanceSignoffRepository.save(clearanceSignoff);
    }

    @Override
    public String studentSignClearance(int clearanceId, String signature) {
        var student = this.userUtil.getCurrentUser();
        if (student == null) throw new UnauthorizedException("Session Expired");

        var clearance = this.clearanceRepository.findById(clearanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance not Found"));

        if (clearance.getStudentSignature() != null) return "This clearance has already signed";

        clearance.setDateOfStudentSignature(LocalDate.now());
        clearance.setStudentSignature(signature);
        clearance.setSubmitted(true);
        this.clearanceRepository.save(clearance);
        return "Clearance has been signed";
    }

    private boolean isSectionSignedAlready(List<ClearanceSignoff> clearanceSignoffs, Role role) {
        for (var clearanceSignoff : clearanceSignoffs) {
            if (clearanceSignoff.getRole().equals(role) && clearanceSignoff.getStatus().equals(ClearanceSignOffStatus.COMPLETED)) {
                return true;
            }
        }
        return false;
    }

    private boolean isClearanceReadyForDSAOrDeanForStudentClearance(
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

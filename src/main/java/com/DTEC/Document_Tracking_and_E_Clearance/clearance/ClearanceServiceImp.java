package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.async.AsyncMessageApi;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignOffStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoffRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.course.CourseRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.InternalServerErrorException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.GenericLetterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.message.MessageService;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    private final AsyncMessageApi asyncMessageApi;
    private final MessageService messageService;

    public ClearanceServiceImp(UserRepository userRepository, ClearanceSignoffRepository clearanceSignoffRepository, ClearanceRepository clearanceRepository, ClearanceMapper clearanceMapper, SchoolYearGenerator schoolYearGenerator, UserUtil userUtil, ClubRepository clubRepository, CourseRepository courseRepository, AsyncMessageApi asyncMessageApi, MessageService messageService) {
        this.userRepository = userRepository;
        this.clearanceSignoffRepository = clearanceSignoffRepository;
        this.clearanceRepository = clearanceRepository;
        this.clearanceMapper = clearanceMapper;
        this.schoolYearGenerator = schoolYearGenerator;
        this.userUtil = userUtil;
        this.clubRepository = clubRepository;
        this.courseRepository = courseRepository;
        this.asyncMessageApi = asyncMessageApi;
        this.messageService = messageService;
    }

    @Transactional
    @Override
    public String releaseStudentClearances() {
        try {
            var students = this.userRepository.findAllStudents();
            if (students.isEmpty())
                throw new ResourceNotFoundException("No Registered Student yet");

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

            Set<String> contactNumbers = new HashSet<>();

            List<ClearanceSignoff> clearanceSignoffs = new ArrayList<>();
            for (var savedClearance : savedClearances) {
                var cs1 = ClearanceUtil.getClearanceSignoff(dsa, savedClearance);
                var cs2 = ClearanceUtil.getClearanceSignoff(guidance, savedClearance);
                var cs3 = ClearanceUtil.getClearanceSignoff(cashier, savedClearance);
                var cs4 = ClearanceUtil.getClearanceSignoff(librarian, savedClearance);
                var cs5 = ClearanceUtil.getClearanceSignoff(schoolNurse, savedClearance);
                var cs6 = ClearanceUtil.getClearanceSignoff(registrar, savedClearance);

                // get the user of this clearance
                var student = savedClearance.getUser();

                // add the contact number of the current user
                contactNumbers.add(student.getContactNumber());

                var studentCourse = student.getCourse();

                List<User> laboratoriesForYearOneAndTwoStudents = getLaboratoriesBasedOnTheStudentYearLevel(student, allOfficeInCharge);
                // check if null meaning the student should need to have
                // multiple lab in-charges that will be signing of its clearance
                if (laboratoriesForYearOneAndTwoStudents == null) {
                    // means that the student is either 3rd or 4th year
                    var labInCharge = ClearanceUtil.getLabInChargeBasedOnStudentCourse(allOfficeInCharge, studentCourse.getShortName());

                    var cs9 = ClearanceUtil.getClearanceSignoff(labInCharge, savedClearance);
                    if (cs9 != null)
                        clearanceSignoffs.add(cs9);

                } else {
                    // means that the student is either 1st or 2nd year
                    // add all the necessary lab in-charges
                    for (User laboratoriesForYearOneAndTwoStudent : laboratoriesForYearOneAndTwoStudents) {
                        var tempCS = ClearanceUtil.getClearanceSignoff(laboratoriesForYearOneAndTwoStudent, savedClearance);
                        clearanceSignoffs.add(tempCS);
                    }
                }

                // get the program head of this user
                var programHead = student.getCourse().getUsers()
                        .stream()
                        .filter(u -> u.getRole().equals(Role.PROGRAM_HEAD))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Program Head's account is not created yet"));
                var cs7 = ClearanceUtil.getClearanceSignoff(programHead, savedClearance);

                // get dean of this user
                var dean = student.getDepartment().getUsers()
                        .stream()
                        .filter(u -> u.getRole().equals(Role.DEAN))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("The clearance can't release due to the Dean's account is not created yet"));
                ;
                var cs8 = ClearanceUtil.getClearanceSignoff(dean, savedClearance);

                clearanceSignoffs.addAll(List.of(
                        cs1,
                        cs2,
                        cs3,
                        cs4,
                        cs5,
                        cs6,
                        cs7,
                        cs8
                ));
            }
            this.clearanceSignoffRepository.saveAll(clearanceSignoffs);

            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // notify all OICs
                        notifyAllOICWhenAllClearancesAreReleased(allOfficeInCharge);

                        String message = GenericLetterUtil.generateMessageAfterClearanceReleased();
                        asyncMessageApi.notifyAllUsers(new ArrayList<>(contactNumbers), message);
                    }
                });
            }

            return "Clearances Successfully Released";
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong");
        }
    }

    private List<User> getLaboratoriesBasedOnTheStudentYearLevel(User user, List<User> OICs) {
        if (!user.getRole().equals(Role.STUDENT) && !user.getRole().equals(Role.STUDENT_OFFICER))
            throw new ForbiddenException("The System can't Generate desire Lab In-Charge.");

        List<User> extractedLabInCharges = OICs
                .stream()
                .filter(oic -> UserUtil.getLabInChargeRoles().contains(oic.getRole()))
                .toList();

        List<User> newLabInCharges = new ArrayList<>();

        var course = user.getCourse();

        switch (user.getYearLevel()) {
            case 1 -> {
                // this lab in-charge will automatically be added if the student is first year
                var scienceLab = extractedLabInCharges
                        .stream()
                        .filter(el -> el.getRole().equals(Role.SCIENCE_LAB))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Science Laboratory In-Charge not Found"));

                // this should trigger if the course is
                if (course.getShortName().equals("BSCpE") || course.getShortName().equals("BSCS")) {
                    var computerLab = extractedLabInCharges
                            .stream()
                            .filter(el -> el.getRole().equals(Role.COMPUTER_SCIENCE_LAB))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Computer Science Laboratory In-Charge not Found"));

                    var ecLab = extractedLabInCharges
                            .stream()
                            .filter(el -> el.getRole().equals(Role.ELECTRONICS_LAB))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Electronics Laboratory In-Charge not Found"));

                    newLabInCharges.addAll(List.of(computerLab, ecLab));
                }

                newLabInCharges.add(scienceLab);
            }
            case 2 -> {
                var computerLab = extractedLabInCharges
                        .stream()
                        .filter(el -> el.getRole().equals(Role.COMPUTER_SCIENCE_LAB))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Computer Science Laboratory In-Charge not Found"));

                var ecLab = extractedLabInCharges
                        .stream()
                        .filter(el -> el.getRole().equals(Role.ELECTRONICS_LAB))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Electronics Laboratory In-Charge not Found"));

                newLabInCharges.addAll(List.of(computerLab, ecLab));
            }
            default -> {
                return null;
            }
        }

        return newLabInCharges;
    }

    private void sendMessageToTheOwnerOfClearanceWhenCompleted(User user, GenericLetterUtil.MessageType messageType) {
        String fullName = UserUtil.getUserFullName(user);
        String message = GenericLetterUtil.generateMessageWhenTheClearanceIsCompleted(fullName, messageType);
        this.messageService.sendMessage(user.getContactNumber(), message);
    }

    private void notifyAllOICWhenAllClearancesAreReleased(List<User> OICs) {
        OICs.forEach(oic -> {
            String fullName = UserUtil.getUserFullName(oic);
            String message = GenericLetterUtil.generateMessageForOfficeInChargeWhenClearancesRelease(fullName);
            this.messageService.sendMessage(oic.getContactNumber(), message);
        });
    }

    public List<ClearanceResponseDto> getAllStudentCompletedClearances() {
        var clearances = this.clearanceRepository.findAllStudentCompletedClearances();
        return this.clearanceMapper.toClearanceResponseDtoList(clearances);
    }

    @Override
    public void confirmClearance(int clearanceId) {
        var clearance = this.clearanceRepository.findById(clearanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance not Found"));

//        var user = clearance.getUser();
//        String message = GenericLetterUtil.generateMessageForUserWhoSubmittedClearance(UserUtil.getUserFullName(user));
//        this.messageService.sendMessage(user.getContactNumber(), message);

        clearance.setClearancePermitReleased(true);
        this.clearanceRepository.save(clearance);
    }

    @Transactional
    @Override
    public String releasePersonnelClearances() {
        try {
            var allPersonnel = this.userRepository.findAllPersonnel();
            if (allPersonnel.isEmpty())
                throw new ResourceNotFoundException("No Registered Personnel yet");

            // fetch all Office in-charges
            var allOfficeInCharge = this.userRepository.findAllOfficeInChargeForPersonnelClearance();

            var multimedia = UserUtil.getUserByRole(allOfficeInCharge, Role.MULTIMEDIA, "Multimedia");
            var librarian = UserUtil.getUserByRole(allOfficeInCharge, Role.LIBRARIAN, "Librarian");
            var cashier = UserUtil.getUserByRole(allOfficeInCharge, Role.CASHIER, "Cashier");
            var registrar = UserUtil.getUserByRole(allOfficeInCharge, Role.REGISTRAR, "Registrar");
            var accountingClerk = UserUtil.getUserByRole(allOfficeInCharge, Role.ACCOUNTING_CLERK, "Accounting Clerk");
            var finance = UserUtil.getUserByRole(allOfficeInCharge, Role.FINANCE, "Finance");
            var custodian = UserUtil.getUserByRole(allOfficeInCharge, Role.CUSTODIAN, "Property Custodian");
            var vpaf = UserUtil.getUserByRole(allOfficeInCharge, Role.VPAF, "VPAF");
            var vpa = UserUtil.getUserByRole(allOfficeInCharge, Role.VPA, "VPA");
            var president = UserUtil.getUserByRole(allOfficeInCharge, Role.PRESIDENT, "President");

            List<Clearance> clearances = allPersonnel
                    .stream()
                    .map(personnel -> Clearance.builder()
                            .schoolYear(this.schoolYearGenerator.generateSchoolYear())
                            .user(personnel)
                            .type(ClearanceType.PERSONNEL_CLEARANCE)
                            .status(ClearanceStatus.PENDING)
                            .build()).toList();
            var savedClearances = this.clearanceRepository.saveAll(clearances);

            List<String> contactNumbers = new ArrayList<>();

            List<ClearanceSignoff> clearanceSignoffs = new ArrayList<>();
            for (var savedClearance : savedClearances) {
                var personnel = savedClearance.getUser();

                // add the contact number of the current user
                contactNumbers.add(personnel.getContactNumber());

                var cs1 = ClearanceUtil.getClearanceSignoff(multimedia, savedClearance);
                var cs2 = ClearanceUtil.getClearanceSignoff(librarian, savedClearance);
                var cs3 = ClearanceUtil.getClearanceSignoff(cashier, savedClearance);
                var cs4 = ClearanceUtil.getClearanceSignoff(registrar, savedClearance);
                var cs5 = ClearanceUtil.getClearanceSignoff(accountingClerk, savedClearance);
                var cs6 = ClearanceUtil.getClearanceSignoff(finance, savedClearance);
                var cs7 = ClearanceUtil.getClearanceSignoff(custodian, savedClearance);
                var cs8 = ClearanceUtil.getClearanceSignoff(vpaf, savedClearance);
                var cs9 = ClearanceUtil.getClearanceSignoff(vpa, savedClearance);
                var cs10 = ClearanceUtil.getClearanceSignoff(president, savedClearance);

                if ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.ACADEMIC)) || personnel.getRole().equals(Role.MODERATOR)) {
                    // this needs to specify the program head receiver of the personnel
                    var course = personnel.getCourse();
                    var usersFromCourse = course.getUsers();
                    var tempProgramHead = UserUtil.getUserByRole(usersFromCourse, Role.PROGRAM_HEAD);
                    var tempCs8 = ClearanceUtil.getClearanceSignoff(tempProgramHead, savedClearance);

                    var labInCharge = ClearanceUtil.getLabInChargeBasedOnStudentCourse(allOfficeInCharge, course.getShortName());
                    var cs13 = ClearanceUtil.getClearanceSignoff(labInCharge, savedClearance);

                    var department = personnel.getDepartment();
                    var usersFromDepartment = department.getUsers();
                    var tempDean = UserUtil.getUserByRole(usersFromDepartment, Role.DEAN);
                    var tempCs9 = ClearanceUtil.getClearanceSignoff(tempDean, savedClearance);
                    var tempCs10 = ClearanceUtil.getClearanceSignoff(vpaf, savedClearance);
                    var cs11 = ClearanceUtil.getClearanceSignoff(vpa, savedClearance);
                    var cs12 = ClearanceUtil.getClearanceSignoff(president, savedClearance);

                    if (cs13 != null)
                        clearanceSignoffs.add(cs13);

                    clearanceSignoffs.addAll(List.of(cs1, cs2, cs3, cs4, cs5, cs6, cs7, tempCs8, tempCs9, tempCs10, cs11, cs12));
                } else if ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.NON_ACADEMIC)) ||
                        UserUtil.getDOGSRoles().contains(personnel.getRole())) { // this condition includes the DSA, Office Head, Guidance, and School Nurse
                    clearanceSignoffs.addAll(List.of(cs1, cs2, cs3, cs4, cs5, cs6, cs7, cs8, cs9, cs10));
                } else if (UserUtil.getOfficeInChargeRoles().contains(personnel.getRole())) {
                    var csMe = ClearanceUtil.getClearanceSignoff(personnel, savedClearance);
                    String signature = this.userUtil.getOfficeInChargeSignature(personnel);
                    csMe.setSignature(signature); // Automatically set their own signature
                    csMe.setStatus(ClearanceSignOffStatus.COMPLETED);
                    csMe.setDateAndTimeOfSignature(LocalDateTime.now());

                    if (UserUtil.getLabInChargeRoles().contains(personnel.getRole())) {
                        var course = personnel.getCourse();
                        var usersFromCourse = course.getUsers();
                        var tempProgramHead = UserUtil.getUserByRole(usersFromCourse, Role.PROGRAM_HEAD);
                        var tempCs8 = ClearanceUtil.getClearanceSignoff(tempProgramHead, savedClearance);

                        var department = personnel.getDepartment();
                        var usersFromDepartment = department.getUsers();
                        var tempDean = UserUtil.getUserByRole(usersFromDepartment, Role.DEAN);
                        var tempCs9 = ClearanceUtil.getClearanceSignoff(tempDean, savedClearance);

                        clearanceSignoffs.add(tempCs8);
                        clearanceSignoffs.add(tempCs9);
                        clearanceSignoffs.add(csMe);

                        clearanceSignoffs.addAll(List.of(cs1, cs2, cs3, cs4, cs5, cs6, cs7, cs8, cs9, cs10, csMe));
                    } else {
                        var users = UserUtil.getAllUserExceptTo(allPersonnel, new HashSet<>() {
                            {
                                add(Role.OFFICE_HEAD);
                                add(Role.DSA);
                                add(Role.PERSONNEL);
                                add(Role.MODERATOR);
                                add(Role.PROGRAM_HEAD);
                                add(Role.DEAN);
                                add(Role.COMPUTER_SCIENCE_LAB);
                                add(Role.SCIENCE_LAB);
                                add(Role.ELECTRONICS_LAB);
                                add(Role.CRIM_LAB);
                                add(Role.HRM_LAB);
                                add(Role.NURSING_LAB);
                            }
                        });

                        if (!personnel.getRole().equals(Role.DEAN) && !personnel.getRole().equals(Role.PROGRAM_HEAD)) {
                            var tempClearanceSignoffs = new ArrayList<>(users
                                    .stream()
                                    .filter(user -> !personnel.getRole().equals(user.getRole()))
                                    .map(user -> ClearanceUtil.getClearanceSignoff(user, savedClearance)).toList());
                            tempClearanceSignoffs.add(csMe);

                            clearanceSignoffs.addAll(tempClearanceSignoffs);
                        } else {
                            // possible roles are Dean and program head
                            if (personnel.getRole().equals(Role.PROGRAM_HEAD)) {
                                var department = personnel.getDepartment();
                                var usersFromDepartment = department.getUsers();
                                var tempDean = UserUtil.getUserByRole(usersFromDepartment, Role.DEAN);
                                var tempCs9 = ClearanceUtil.getClearanceSignoff(tempDean, savedClearance);

                                var tempClearanceSignoffs = new ArrayList<>(users
                                        .stream()
                                        .map(user -> ClearanceUtil.getClearanceSignoff(user, savedClearance)).toList());
                                tempClearanceSignoffs.add(csMe);
                                tempClearanceSignoffs.add(tempCs9);

                                clearanceSignoffs.addAll(tempClearanceSignoffs);
                            } else {
                                // Dean role
                                var tempClearanceSignoffs = new ArrayList<>(users
                                        .stream()
                                        .map(user -> ClearanceUtil.getClearanceSignoff(user, savedClearance)).toList());
                                tempClearanceSignoffs.add(csMe);
                                clearanceSignoffs.addAll(tempClearanceSignoffs);
                            }
                        }
                    }
                } else {
                    throw new ForbiddenException("Clearance can't release due to invalid role");
                }
            }

            this.clearanceSignoffRepository.saveAll(clearanceSignoffs);

            // Ensure transaction is active before registering synchronization
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // notify all OICs
                        notifyAllOICWhenAllClearancesAreReleased(allOfficeInCharge);

                        String message = GenericLetterUtil.generateMessageAfterClearanceReleased();
                        asyncMessageApi.notifyAllUsers(contactNumbers, message);
                    }
                });
            }

            return "Clearances Successfully Released";
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong");
        }
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
                var personnel = clearance.getUser();

                if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.GUIDANCE,
                            Role.CASHIER,
                            Role.LIBRARIAN,
                            Role.SCHOOL_NURSE,
                            Role.PROGRAM_HEAD,
                            Role.REGISTRAR,
                            Role.DSA
                    )) && ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                } else {
                    if ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.ACADEMIC)) ||
                            personnel.getRole().equals(Role.MODERATOR) || UserUtil.getLabInChargeRoles().contains(personnel.getRole())) {
                        if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                                Role.CASHIER,
                                Role.REGISTRAR,
                                Role.ACCOUNTING_CLERK,
                                Role.FINANCE,
                                Role.CUSTODIAN,
                                Role.PROGRAM_HEAD
                        )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
                            clearanceList.add(clearance);
                        }
                    }
                }

            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.PROGRAM_HEAD)) {
            List<Clearance> clearances = this.clearanceRepository.findAllForProgramHead(user.getId());

            // check each clearance if their signatures completed before proceed to the program head
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.GUIDANCE,
                            Role.CASHIER,
                            Role.LIBRARIAN,
                            Role.SCHOOL_NURSE,
                            Role.REGISTRAR
                    )) && ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                } else {
                    var personnel = clearance.getUser();

                    if ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.ACADEMIC)) ||
                            personnel.getRole().equals(Role.MODERATOR) || UserUtil.getLabInChargeRoles().contains(personnel.getRole())) {
                        clearanceList.add(clearance);
                    }

                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.DSA)) {
            List<Clearance> clearances = this.clearanceRepository.findAllClearanceByType(ClearanceType.STUDENT_CLEARANCE);
            // check each clearance if their signatures completed before proceed to the program head
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
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
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD,
                            Role.DEAN
                    )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.VPA)) {
            var clearances = this.clearanceRepository.findAllClearancesForVPAFAndVPAAndPresident();
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD,
                            Role.DEAN,
                            Role.VPAF
                    )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearanceList.add(clearance);
                    }
                }
            }
            return this.clearanceMapper.toClearanceResponseDtoList(clearanceList);
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            var clearances = this.clearanceRepository.findAllClearancesForVPAFAndVPAAndPresident();
            for (var clearance : clearances) {
                if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.PROGRAM_HEAD,
                            Role.DEAN,
                            Role.VPAF,
                            Role.VPA
                    )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
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
        } else if (user.getRole().equals(Role.MULTIMEDIA)) {
            List<Clearance> clearances = this.clearanceRepository.findAllClearanceByType(ClearanceType.PERSONNEL_CLEARANCE);
            return this.clearanceMapper.toClearanceResponseDtoList(clearances);
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

    @Transactional
    @Override
    public String signClearance(int clearanceId, String signature) {
        var clearance = this.clearanceRepository.findById(clearanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Clearance not Found"));

        if (clearance.getStatus().equals(ClearanceStatus.COMPLETED))
            throw new ForbiddenException("This Clearance is not Modifiable anymore");

        var user = this.userUtil.getCurrentUser();

        if (ClearanceUtil.isSectionSignedAlready(clearance.getClearanceSignoffs(), user.getRole()))
            throw new ForbiddenException("This Section has already been Signed");

        var personnel = clearance.getUser();

//        if (user.getRole().equals(Role.DSA)) {
//            if (isClearanceReadyForDSAOrDeanForStudentClearance(clearance.getClearanceSignoffs(), List.of(
//                    // required signed roles to proceed to dsa
//                    Role.GUIDANCE,
//                    Role.LIBRARIAN,
//                    Role.SCHOOL_NURSE,
//                    Role.REGISTRAR
//            ))) {
//            } else {
//                throw new ForbiddenException("DSA can't sign yet because some required personnel and offices have not signed yet");
//            }
//        } else if (user.getRole().equals(Role.DEAN)) {
//            if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
//                if (isClearanceReadyForDSAOrDeanForStudentClearance(clearance.getClearanceSignoffs(), List.of(
//                        // required signed roles to proceed to dean
//                        Role.GUIDANCE,
//                        Role.LIBRARIAN,
//                        Role.SCHOOL_NURSE,
//                        Role.REGISTRAR,
//                        Role.DSA
//                )) && ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())) {
//                } else {
//                    throw new ForbiddenException("DEAN can't sign yet because some required personnel and offices have not signed yet");
//                }
//            } else {
//                // this is for acad personnel
//                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
//                        // required signed roles to proceed to dean
//                        Role.GUIDANCE,
//                        Role.REGISTRAR,
//                        Role.CASHIER,
//                        Role.ACCOUNTING_CLERK,
//                        Role.FINANCE,
//                        Role.CUSTODIAN,
//                        Role.PROGRAM_HEAD
//                )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs()) &&
//                        ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.ACADEMIC)) ||
//                                personnel.getRole().equals(Role.MODERATOR))) {
//                } else {
//                    throw new ForbiddenException("DEAN can't sign yet because some required Office In-Charge have not signed yet");
//                }
//            }
//        } else if (user.getRole().equals(Role.VPAF)) {
//            if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
//                // check if the personnel is acad
//                if ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.ACADEMIC)) ||
//                        personnel.getRole().equals(Role.MODERATOR)) {
//                    if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
//                            // required signed roles to proceed to dean
//                            Role.GUIDANCE,
//                            Role.REGISTRAR,
//                            Role.CASHIER,
//                            Role.ACCOUNTING_CLERK,
//                            Role.FINANCE,
//                            Role.CUSTODIAN,
//                            Role.PROGRAM_HEAD,
//                            Role.DEAN
//                    )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
//                    } else {
//                        throw new ForbiddenException("VPAF can't sign yet because some required Office In-Charge have not signed yet");
//                    }
//                } else {
//
//                }
//            }
//        } else if (user.getRole().equals(Role.VPA)) {
//            if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
//                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
//                        // required signed roles to proceed to dean
//                        Role.GUIDANCE,
//                        Role.REGISTRAR,
//                        Role.CASHIER,
//                        Role.ACCOUNTING_CLERK,
//                        Role.FINANCE,
//                        Role.CUSTODIAN,
//                        Role.PROGRAM_HEAD,
//                        Role.VPAF
//                )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
//                    // TODO this is just temporary removed, due to the assigning of type of personnel is not yet implemented
////                    ClearanceUtil.isOneOfLabInChargeSigned(clearance.getClearanceSignoffs())
//                } else {
//                    throw new ForbiddenException("VPA can't sign yet because some required Office In-Charge have not signed yet");
//                }
//            }
//        } else if (user.getRole().equals(Role.PRESIDENT)) {
//            if (clearance.getType().equals(ClearanceType.PERSONNEL_CLEARANCE)) {
//                if (ClearanceUtil.isClearanceReadyForDSAOrDeanForPersonnelClearance(clearance.getClearanceSignoffs(), List.of(
//                        // required signed roles to proceed to dean
//                        Role.GUIDANCE,
//                        Role.REGISTRAR,
//                        Role.CASHIER,
//                        Role.ACCOUNTING_CLERK,
//                        Role.FINANCE,
//                        Role.CUSTODIAN,
//                        Role.PROGRAM_HEAD,
//                        Role.DEAN,
//                        Role.VPAF,
//                        Role.VPA
//                )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
//                } else {
//                    throw new ForbiddenException("PRESIDENT can't sign yet because some required Office In-Charge have not signed yet");
//                }
//            }
//        }

        var clearanceSignoff = clearance.getClearanceSignoffs()
                .stream()
                .filter(cs -> cs.getUser().getRole().equals(user.getRole()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Invalid Office In-Charge Assigned for this Clearance"));

        clearanceSignoff.setSignature(signature);
        clearanceSignoff.setStatus(ClearanceSignOffStatus.COMPLETED);
        clearanceSignoff.setDateAndTimeOfSignature(LocalDateTime.now());
        this.clearanceSignoffRepository.save(clearanceSignoff);

        // check if all the needed signatures are completed
        if (clearance.getType().equals(ClearanceType.STUDENT_CLEARANCE)) {
            if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
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
                var clearanceOwner = clearance.getUser();
                sendMessageToTheOwnerOfClearanceWhenCompleted(clearanceOwner, GenericLetterUtil.MessageType.STUDENT);
            }
        } else {
            if ((personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.ACADEMIC)) ||
                    personnel.getRole().equals(Role.MODERATOR) || UserUtil.getLabInChargeRoles().contains(personnel.getRole())) {
                if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
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
                )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
                    clearance.setStatus(ClearanceStatus.COMPLETED);
                    this.clearanceRepository.save(clearance);
                    var clearanceOwner = clearance.getUser();
                    sendMessageToTheOwnerOfClearanceWhenCompleted(clearanceOwner, GenericLetterUtil.MessageType.PERSONNEL);
                }
            } else if (personnel.getRole().equals(Role.PERSONNEL) && personnel.getType().equals(PersonnelType.NON_ACADEMIC)) {
                if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                        Role.CASHIER,
                        Role.REGISTRAR,
                        Role.ACCOUNTING_CLERK,
                        Role.FINANCE,
                        Role.CUSTODIAN,
                        Role.VPAF,
                        Role.VPA,
                        Role.PRESIDENT
                )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
                    clearance.setStatus(ClearanceStatus.COMPLETED);
                    this.clearanceRepository.save(clearance);
                    var clearanceOwner = clearance.getUser();
                    sendMessageToTheOwnerOfClearanceWhenCompleted(clearanceOwner, GenericLetterUtil.MessageType.PERSONNEL);
                }
            } else {
                if (!personnel.getRole().equals(Role.PROGRAM_HEAD) && !personnel.getRole().equals(Role.DEAN)) {
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.VPAF,
                            Role.VPA,
                            Role.PRESIDENT
                    )) && ClearanceUtil.isLibrarianOrMultimediaSigned(clearance.getClearanceSignoffs())) {
                        clearance.setStatus(ClearanceStatus.COMPLETED);
                        this.clearanceRepository.save(clearance);
                        var clearanceOwner = clearance.getUser();
                        sendMessageToTheOwnerOfClearanceWhenCompleted(clearanceOwner, GenericLetterUtil.MessageType.PERSONNEL);
                    }
                } else {
                    // program head and dean
                    if (ClearanceUtil.areAllSignaturesSettled(clearance.getClearanceSignoffs(), List.of(
                            Role.CASHIER,
                            Role.REGISTRAR,
                            Role.ACCOUNTING_CLERK,
                            Role.FINANCE,
                            Role.CUSTODIAN,
                            Role.VPAF,
                            Role.VPA,
                            Role.PRESIDENT
                    ))) {
                        clearance.setStatus(ClearanceStatus.COMPLETED);
                        this.clearanceRepository.save(clearance);
                        var clearanceOwner = clearance.getUser();
                        sendMessageToTheOwnerOfClearanceWhenCompleted(clearanceOwner, GenericLetterUtil.MessageType.PERSONNEL);
                    }
                }
            }

        }
        return "Signature has been attached";
    }

    @Override
    public ClearanceResponseDto getNewClearance() {
        try {
            var user = this.userUtil.getCurrentUser();
            if (user == null) throw new UnauthorizedException("Session Expired");

            var clearancePage = this.clearanceRepository.findClearanceByUserId(user.getId());
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
                .orElseThrow(() -> new ResourceNotFoundException("You can't sign this Clearance"));

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

    @Override
    public List<ClearanceResponseDto> search(String query) {
        var clearances = this.clearanceRepository.findAllBySearch(query);
        return this.clearanceMapper.toClearanceResponseDtoList(clearances);
    }
}

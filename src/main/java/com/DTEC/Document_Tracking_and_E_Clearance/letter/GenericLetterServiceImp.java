package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposalRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterType;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampusRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampusRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter.PermitToEnter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter.PermitToEnterRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment.SFEF;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment.SFEFRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.message.MessageService;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GenericLetterServiceImp implements GenericLetterService {

    private final BudgetProposalRepository budgetProposalRepository;
    private final ImplementationLetterInCampusRepository implementationLetterInCampusRepository;
    private final ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository;
    private final CommunicationLetterRepository communicationLetterRepository;
    private final PermitToEnterRepository permitToEnterRepository;
    private final SFEFRepository sfefRepository;

    private final SignedPeopleMapper signedPeopleMapper;

    private final UserUtil userUtil;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final SignedPeopleRepository signedPeopleRepository;
    private final MessageService messageService;
    private final UserRepository userRepository;

    public GenericLetterServiceImp(BudgetProposalRepository budgetProposalRepository, ImplementationLetterInCampusRepository implementationLetterInCampusRepository, ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository, CommunicationLetterRepository communicationLetterRepository, PermitToEnterRepository permitToEnterRepository, SFEFRepository sfefRepository, SignedPeopleMapper signedPeopleMapper, UserUtil userUtil, DateTimeFormatterUtil dateTimeFormatterUtil, SignedPeopleRepository signedPeopleRepository, MessageService messageService, UserRepository userRepository) {
        this.budgetProposalRepository = budgetProposalRepository;
        this.implementationLetterInCampusRepository = implementationLetterInCampusRepository;
        this.implementationLetterOffCampusRepository = implementationLetterOffCampusRepository;
        this.communicationLetterRepository = communicationLetterRepository;
        this.permitToEnterRepository = permitToEnterRepository;
        this.sfefRepository = sfefRepository;
        this.signedPeopleMapper = signedPeopleMapper;
        this.userUtil = userUtil;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.signedPeopleRepository = signedPeopleRepository;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @Override
    public List<GenericResponse> getAllLetters(int s) {
        if (s > 50) throw new ForbiddenException("Maximum size exceeded");

        List<GenericResponse> genericResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 50);

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Invalid User");

        if (user.getRole().equals(Role.MODERATOR) || user.getRole().equals(Role.STUDENT_OFFICER)) {
            if (!user.getRole().equals(Role.MODERATOR)) {
                Page<ImplementationLetterOffCampus> implementationLetterOffCampusPage = this.implementationLetterOffCampusRepository.findAll(pageable, user.getId(), user.getRole().name());
                implementationLetterOffCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));
            }

            Page<BudgetProposal> budgetProposalPage = this.budgetProposalRepository.findAll(pageable, user.getId(), user.getRole().name());
            budgetProposalPage.getContent().forEach(b -> genericResponses.add(transformToGeneric(b)));

            Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(pageable, user.getId(), user.getRole().name());
            communicationLetterPage.getContent().forEach(c -> genericResponses.add(transformToGeneric(c, c)));

            Page<ImplementationLetterInCampus> implementationLetterInCampusPage = this.implementationLetterInCampusRepository.findAll(pageable, user.getId(), user.getRole().name());
            implementationLetterInCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

            Page<PermitToEnter> permitToEnterPage = this.permitToEnterRepository.findAll(pageable, user.getId(), user.getRole().name());
            permitToEnterPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

            Page<SFEF> sfefPage = this.sfefRepository.findAll(pageable, user.getId(), user.getRole().name());
            sfefPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));
        } else if (user.getRole().equals(Role.MULTIMEDIA) || user.getRole().equals(Role.CHAPEL)) {
            Page<SFEF> sfefPage = this.sfefRepository.findAll(pageable, user.getId());
            sfefPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));
        } else {
            String stringRole = user.getRole().name();

            Page<BudgetProposal> budgetProposalPage = this.budgetProposalRepository.findAll(stringRole, pageable, user.getId());
            budgetProposalPage.getContent().forEach(b -> genericResponses.add(transformToGeneric(b)));

            Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(stringRole, pageable, user.getId());
            communicationLetterPage.getContent().forEach(c -> genericResponses.add(transformToGeneric(c, c)));

            Page<ImplementationLetterInCampus> implementationLetterInCampusPage = this.implementationLetterInCampusRepository.findAll(stringRole, pageable, user.getId());
            implementationLetterInCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

            Page<ImplementationLetterOffCampus> implementationLetterOffCampusPage = this.implementationLetterOffCampusRepository.findAll(stringRole, pageable, user.getId());
            implementationLetterOffCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

            Page<PermitToEnter> permitToEnterPage = this.permitToEnterRepository.findAll(stringRole, pageable, user.getId());
            permitToEnterPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

            Page<SFEF> sfefPage = this.sfefRepository.findAll(stringRole, pageable, user.getId());
            sfefPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));
        }

        // sort the gathered data
        genericResponses.sort(Comparator.comparing(GenericResponse::getCreatedDate).reversed());

        // Return a sublist for the specified range
        return genericResponses.subList(0, Math.min(s, genericResponses.size()));
    }

    @Transactional
    @Override
    public void onClick(TypeOfLetter type, int id) {
        switch (type) {
            case BUDGET_PROPOSAL:
                budgetProposalOnClick(id);
                break;
            case COMMUNICATION_LETTER:
                communicationLetterOnClick(id);
                break;
            case IMPLEMENTATION_LETTER_IN_CAMPUS:
                implementationLetterInCampusOnClick(id);
                break;
            case IMPLEMENTATION_LETTER_OFF_CAMPUS:
                implementationLetterOffCampusOnClick(id);
                break;
            case PERMIT_TO_ENTER:
                permitToEnterOnClick(id);
                break;
            case SFEF:
                SFEFOnClick(id);
                break;
            default:
                throw new ForbiddenException("Invalid Type of Letter");
        }
    }

    private void budgetProposalOnClick(int id) {
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Budget Proposal"));

        // this avoids letter manipulation if it is completed or declined
        if (budgetProposal.getStatus().equals(LetterStatus.COMPLETED) ||
                budgetProposal.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var signedPerson = budgetProposal.getSignedPeople()
                .stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId())).findFirst();

        if (signedPerson.isPresent()) return;

        if (!budgetProposal.getCurrentLocation().name().equals(user.getRole().name())) return;

        var signedPeople = budgetProposal.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole()))
                .findFirst();

        if (signedPeople.isEmpty()) throw new ForbiddenException("Invalid Assigned Office In-Charge for this letter");

        var tempSignedPerson = signedPeople.get();
        tempSignedPerson.setUser(user);
        tempSignedPerson.setStatus(SignedPeopleStatus.IN_PROGRESS);

        if (budgetProposal.getStatus().equals(LetterStatus.FOR_EVALUATION))
            budgetProposal.setStatus(LetterStatus.IN_PROGRESS);

        this.budgetProposalRepository.save(budgetProposal);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void communicationLetterOnClick(int id) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Invalid Communication Letter"));

        // this avoids letter manipulation if it is completed or declined
        if (communicationLetter.getStatus().equals(LetterStatus.COMPLETED) ||
                communicationLetter.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var signedPerson = communicationLetter.getSignedPeople()
                .stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId())).findFirst();

        if (signedPerson.isPresent()) return;

        if (!communicationLetter.getCurrentLocation().name().equals(user.getRole().name())) return;

        var signedPeople = communicationLetter.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole()))
                .findFirst();

        if (signedPeople.isEmpty()) throw new ForbiddenException("Invalid Assigned Office In-Charge for this letter");

        var tempSignedPerson = signedPeople.get();
        tempSignedPerson.setUser(user);
        tempSignedPerson.setStatus(SignedPeopleStatus.IN_PROGRESS);

        if (communicationLetter.getStatus().equals(LetterStatus.FOR_EVALUATION))
            communicationLetter.setStatus(LetterStatus.IN_PROGRESS);

        this.communicationLetterRepository.save(communicationLetter);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void implementationLetterInCampusOnClick(int id) {
        var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Implementation Letter In Campus"));

        // this avoids letter manipulation if it is completed or declined
        if (implementationLetterInCampus.getStatus().equals(LetterStatus.COMPLETED) ||
                implementationLetterInCampus.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var signedPerson = implementationLetterInCampus.getSignedPeople()
                .stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId())).findFirst();

        if (signedPerson.isPresent()) return;

        if (!implementationLetterInCampus.getCurrentLocation().name().equals(user.getRole().name())) return;

        var signedPeople = implementationLetterInCampus.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole()))
                .findFirst();

        if (signedPeople.isEmpty()) throw new ForbiddenException("Invalid Assigned Office In-Charge for this letter");

        var tempSignedPerson = signedPeople.get();
        tempSignedPerson.setUser(user);
        tempSignedPerson.setStatus(SignedPeopleStatus.IN_PROGRESS);

        if (implementationLetterInCampus.getStatus().equals(LetterStatus.FOR_EVALUATION))
            implementationLetterInCampus.setStatus(LetterStatus.IN_PROGRESS);

        this.implementationLetterInCampusRepository.save(implementationLetterInCampus);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void implementationLetterOffCampusOnClick(int id) {
        var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Implementation Letter Off Campus"));

        // this avoids letter manipulation if it is completed or declined
        if (implementationLetterOffCampus.getStatus().equals(LetterStatus.COMPLETED) ||
                implementationLetterOffCampus.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();

        var signedPerson = implementationLetterOffCampus.getSignedPeople()
                .stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId())).findFirst();

        if (signedPerson.isPresent()) return;

        if (!implementationLetterOffCampus.getCurrentLocation().name().equals(user.getRole().name())) return;

        var signedPeople = implementationLetterOffCampus.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole()))
                .findFirst();

        if (signedPeople.isEmpty()) throw new ForbiddenException("Invalid Assigned Office In-Charge for this letter");

        var tempSignedPerson = signedPeople.get();
        tempSignedPerson.setUser(user);
        tempSignedPerson.setStatus(SignedPeopleStatus.IN_PROGRESS);

        if (implementationLetterOffCampus.getStatus().equals(LetterStatus.FOR_EVALUATION))
            implementationLetterOffCampus.setStatus(LetterStatus.IN_PROGRESS);

        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void permitToEnterOnClick(int id) {
        var permitToEnter = this.permitToEnterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit To Enter not Found"));

        // this avoids letter manipulation if it is completed or declined
        if (permitToEnter.getStatus().equals(LetterStatus.COMPLETED) ||
                permitToEnter.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();

        var signedPerson = permitToEnter.getSignedPeople()
                .stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId())).findFirst();

        if (signedPerson.isPresent()) return;

        if (!permitToEnter.getCurrentLocation().name().equals(user.getRole().name())) return;

        var signedPeople = permitToEnter.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole()))
                .findFirst();

        if (signedPeople.isEmpty()) throw new ForbiddenException("Invalid Assigned Office In-Charge for this letter");

        var tempSignedPerson = signedPeople.get();
        tempSignedPerson.setUser(user);
        tempSignedPerson.setStatus(SignedPeopleStatus.IN_PROGRESS);

        if (permitToEnter.getStatus().equals(LetterStatus.FOR_EVALUATION))
            permitToEnter.setStatus(LetterStatus.IN_PROGRESS);

        this.permitToEnterRepository.save(permitToEnter);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void SFEFOnClick(int id) {
        var sfef = this.sfefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SFEF not Found"));

        // this avoids letter manipulation if it is completed or declined
        if (sfef.getStatus().equals(LetterStatus.COMPLETED) ||
                sfef.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();

        var signedPerson = sfef.getSignedPeople()
                .stream()
                .filter(s -> s.getUser() != null && s.getUser().getId().equals(user.getId())).findFirst();

        if (signedPerson.isPresent()) return;

        if (!sfef.getCurrentLocation().name().equals(user.getRole().name())) return;

        var signedPeople = sfef.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole()))
                .findFirst();

        if (signedPeople.isEmpty()) throw new ForbiddenException("Invalid Assigned Office In-Charge for this letter");

        var tempSignedPerson = signedPeople.get();
        tempSignedPerson.setUser(user);
        tempSignedPerson.setStatus(SignedPeopleStatus.IN_PROGRESS);

        if (sfef.getStatus().equals(LetterStatus.FOR_EVALUATION))
            sfef.setStatus(LetterStatus.IN_PROGRESS);

        this.sfefRepository.save(sfef);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private GenericResponse transformToGeneric(SharedFields object) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("date_requested", this.dateTimeFormatterUtil.formatIntoDateTime(object.getCreatedAt()));
        fields.put("letter_type", object.getType());
        fields.put("name_of_transaction", object.getNameOfTransaction());
        fields.put("requested_by", object.getClub().getName());
        fields.put("status", object.getStatus());
        fields.put("last_update", this.dateTimeFormatterUtil.formatIntoDateTime(object.getLastModified()));
        fields.put("current_location", object.getCurrentLocation());
        fields.put("reason_of_rejection", object.getReasonOfRejection());

        return GenericResponse.builder()
                .id(object.getId())
                .type(object.getType())
                .fields(fields)
                .signedPeople(this.signedPeopleMapper.toSignedPeopleResponseDtoList(object.getSignedPeople()))
                .createdDate(object.getCreatedAt())
                .build();
    }

    private GenericResponse transformToGeneric(SharedFields object, Object cml) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("date_requested", this.dateTimeFormatterUtil.formatIntoDateTime(object.getCreatedAt()));
        fields.put("letter_type", object.getType());
        fields.put("name_of_transaction", object.getNameOfTransaction());
        fields.put("requested_by", object.getClub().getName());
        fields.put("status", object.getStatus());
        fields.put("last_update", this.dateTimeFormatterUtil.formatIntoDateTime(object.getLastModified()));
        fields.put("current_location", object.getCurrentLocation());
        fields.put("reason_of_rejection", object.getReasonOfRejection());

        return GenericResponse.builder()
                .id(object.getId())
                .type(object.getType())
                .fields(fields)
                .createdDate(object.getCreatedAt())
                .signedPeople(this.signedPeopleMapper.toSignedPeopleResponseDtoList(object.getSignedPeople()))
                .cml(cml instanceof CommunicationLetter ? ((CommunicationLetter) cml).getTypeOfCampus().name() : "")
                .build();
    }

    @Transactional
    @Override
    public void signLetter(TypeOfLetter type, String signature, int letterId) {
        var user = this.userUtil.getCurrentUser();

        if (user == null) throw new ResourceNotFoundException("User not Found");

        switch (type) {
            case BUDGET_PROPOSAL:
                signatureForBPLetter(signature, letterId, user);
                break;
            case COMMUNICATION_LETTER:
                signatureForCLLetter(signature, letterId, user);
                break;
            case IMPLEMENTATION_LETTER_IN_CAMPUS:
                signatureForILICLetter(signature, letterId, user);
                break;
            case IMPLEMENTATION_LETTER_OFF_CAMPUS:
                signatureForILOCLetter(signature, letterId, user);
                break;
            case PERMIT_TO_ENTER:
                signatureForPermitToEnter(signature, letterId, user);
                break;
            case SFEF:
                signatureForSFEF(signature, letterId, user);
                break;
            default:
                throw new ForbiddenException("Invalid Type of Letter");
        }
    }

    private void signatureForBPLetter(String signature, int id, User user) {
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget Proposal not Found"));

        Optional<SignedPeople> optionalSignedPerson = budgetProposal.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole())).findFirst();

        if (optionalSignedPerson.isEmpty()) throw new ForbiddenException("Invalid Assigned Officer for this Letter");

        var signedPerson = optionalSignedPerson.get();
        signedPerson.setSignature(signature);
        signedPerson.setUser(user);

        if (signedPerson.getStatus().equals(SignedPeopleStatus.EVALUATED))
            throw new ForbiddenException("You already Signed this Letter");

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the user has already been signed
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            budgetProposal.setCurrentLocation(CurrentLocation.DSA);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(budgetProposal);
            sendMessageToOICWhenReceivesATransactionLetter(budgetProposal, Role.DSA);

        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(budgetProposal, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            budgetProposal.setCurrentLocation(CurrentLocation.FINANCE);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(budgetProposal);
            sendMessageToOICWhenReceivesATransactionLetter(budgetProposal, Role.FINANCE);
        } else if (user.getRole().equals(Role.FINANCE)) {
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(budgetProposal, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            if (getSignedPerson(budgetProposal, Role.DSA).isEmpty())
                throw new ForbiddenException("The DSA doesn't signed yet");

            budgetProposal.setCurrentLocation(CurrentLocation.PRESIDENT);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(budgetProposal);
            sendMessageToOICWhenReceivesATransactionLetter(budgetProposal, Role.PRESIDENT);

        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(budgetProposal, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            if (getSignedPerson(budgetProposal, Role.DSA).isEmpty())
                throw new ForbiddenException("The DSA doesn't signed yet");

            if (getSignedPerson(budgetProposal, Role.FINANCE).isEmpty())
                throw new ForbiddenException("The Finance doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            budgetProposal.setStatus(LetterStatus.COMPLETED);

            // notify student officer
            sendMessageToStudentOfficer(budgetProposal);
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }

        this.budgetProposalRepository.save(budgetProposal);
        this.signedPeopleRepository.save(signedPerson);
    }

    // This sends to student officer
    private void sendMessageToStudentOfficer(SharedFields sharedFields) {
        var studentOfficer = GenericLetterUtil.getUserByRole(sharedFields.getSignedPeople(), Role.STUDENT_OFFICER);

        if (studentOfficer != null) {
            final String message;
            if (sharedFields.getStatus().equals(LetterStatus.COMPLETED) || sharedFields.getStatus().equals(LetterStatus.DECLINED)) {
                message = GenericLetterUtil.generateMessageForFinalDecisionOfLetter(UserUtil.getUserFullName(studentOfficer), sharedFields);
            }else{
                message = GenericLetterUtil.generateMessageWhenLetterIsSubmittedOrMovesToTheNextOffice(UserUtil.getUserFullName(studentOfficer), sharedFields);
            }
            this.messageService.sendMessage(studentOfficer.getContactNumber(), message);
        }
    }

    private void sendMessageToOICWhenReceivesATransactionLetter(SharedFields sharedFields, Role role) {
        var OICs = this.userRepository.findUsersByRole(role);
        if (!OICs.isEmpty()) {
            OICs.forEach(oic -> {
                String message = GenericLetterUtil.generateMessageWhenLetterIsSubmittedOrMovesToTheNextOffice(UserUtil.getUserFullName(oic), sharedFields);
                this.messageService.sendMessage(oic.getContactNumber(), message);
            });
        }
    }

    private User extractUserNumber(SharedFields sharedFields, Role role) {
        var signedPeople = sharedFields.getSignedPeople();
        var signedPerson = signedPeople
                .stream()
                .filter(sp -> sp.getUser() != null && sp.getUser().getRole().equals(role))
                .findFirst()
                .orElse(null);
        if (signedPerson != null)
            return signedPerson.getUser();
        else
            return null;
    }

    private Optional<SignedPeople> getSignedPerson(SharedFields object, Role role) {
        return object.getSignedPeople()
                .stream().filter(s -> s.getRole().equals(role)).findFirst();
    }

    private void signatureForCLLetter(String signature, int id, User user) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        Optional<SignedPeople> optionalSignedPerson = communicationLetter.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole())).findFirst();

        if (optionalSignedPerson.isEmpty()) throw new ForbiddenException("Invalid Assigned Officer for this Letter");

        var signedPerson = optionalSignedPerson.get();
        signedPerson.setSignature(signature);
        signedPerson.setUser(user);

        if (signedPerson.getStatus().equals(SignedPeopleStatus.EVALUATED))
            throw new ForbiddenException("You already Signed this Letter");

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the user has already been signed
            if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            communicationLetter.setCurrentLocation(CurrentLocation.DSA);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(communicationLetter);
            sendMessageToOICWhenReceivesATransactionLetter(communicationLetter, Role.DSA);
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(communicationLetter, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            if (communicationLetter.getTypeOfCampus().equals(CommunicationLetterType.IN_CAMPUS)) {
                communicationLetter.setCurrentLocation(CurrentLocation.PRESIDENT);
                sendMessageToOICWhenReceivesATransactionLetter(communicationLetter, Role.PRESIDENT);
            } else {
                communicationLetter.setCurrentLocation(CurrentLocation.OFFICE_HEAD);
                sendMessageToOICWhenReceivesATransactionLetter(communicationLetter, Role.OFFICE_HEAD);
            }

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(communicationLetter);
        } else if (user.getRole().equals(Role.PRESIDENT) || user.getRole().equals(Role.OFFICE_HEAD)) {
            // this only applicable to communication letter in campus
            if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(communicationLetter, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            if (getSignedPerson(communicationLetter, Role.DSA).isEmpty())
                throw new ForbiddenException("The DSA doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            communicationLetter.setStatus(LetterStatus.COMPLETED);

            // notify the moderator and student officer
            sendMessageToStudentOfficer(communicationLetter);
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }
        this.communicationLetterRepository.save(communicationLetter);
        this.signedPeopleRepository.save(signedPerson);
    }

    private void signatureForILICLetter(String signature, int id, User user) {
        var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        Optional<SignedPeople> optionalSignedPerson = implementationLetterInCampus.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole())).findFirst();

        if (optionalSignedPerson.isEmpty()) throw new ForbiddenException("Invalid Assigned Officer for this Letter");

        var signedPerson = optionalSignedPerson.get();
        signedPerson.setSignature(signature);
        signedPerson.setUser(user);

        if (signedPerson.getStatus().equals(SignedPeopleStatus.EVALUATED))
            throw new ForbiddenException("You already Signed this Letter");

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the user has already been signed
            if (getSignedPerson(implementationLetterInCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            implementationLetterInCampus.setCurrentLocation(CurrentLocation.DSA);
            sendMessageToStudentOfficer(implementationLetterInCampus);
            sendMessageToOICWhenReceivesATransactionLetter(implementationLetterInCampus, Role.DSA);
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(implementationLetterInCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(implementationLetterInCampus, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            implementationLetterInCampus.setStatus(LetterStatus.COMPLETED);
            sendMessageToStudentOfficer(implementationLetterInCampus);
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }
        this.implementationLetterInCampusRepository.save(implementationLetterInCampus);
        this.signedPeopleRepository.save(signedPerson);
    }

    private void signatureForILOCLetter(String signature, int id, User user) {
        var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        Optional<SignedPeople> optionalSignedPerson = implementationLetterOffCampus.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole())).findFirst();

        if (optionalSignedPerson.isEmpty()) throw new ForbiddenException("Invalid Assigned Officer for this Letter");

        var signedPerson = optionalSignedPerson.get();
        signedPerson.setSignature(signature);
        signedPerson.setUser(user);

        if (signedPerson.getStatus().equals(SignedPeopleStatus.EVALUATED))
            throw new ForbiddenException("You already Signed this Letter");

        if (user.getRole().equals(Role.OFFICE_HEAD)) {
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            implementationLetterOffCampus.setCurrentLocation(CurrentLocation.PRESIDENT);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(implementationLetterOffCampus);
            sendMessageToOICWhenReceivesATransactionLetter(implementationLetterOffCampus, Role.PRESIDENT);
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            if (getSignedPerson(implementationLetterOffCampus, Role.OFFICE_HEAD).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            implementationLetterOffCampus.setStatus(LetterStatus.COMPLETED);
            sendMessageToStudentOfficer(implementationLetterOffCampus);
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }
        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);
        this.signedPeopleRepository.save(signedPerson);
    }

    private void signatureForPermitToEnter(String signature, int id, User user) {
        var permitToEnter = this.permitToEnterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit To Enter not Found"));

        Optional<SignedPeople> optionalSignedPerson = permitToEnter.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole())).findFirst();

        if (signature == null || signature.isEmpty())
            throw new ForbiddenException("Please Attach your Signature First!");

        if (optionalSignedPerson.isEmpty()) throw new ForbiddenException("Invalid Assigned Officer for this Letter");

        var signedPerson = optionalSignedPerson.get();
        signedPerson.setSignature(signature);
        signedPerson.setUser(user);

        if (user.getRole().equals(Role.MODERATOR)) {
            permitToEnter.setCurrentLocation(CurrentLocation.OFFICE_HEAD);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(permitToEnter);
            sendMessageToOICWhenReceivesATransactionLetter(permitToEnter, Role.OFFICE_HEAD);
        } else if (user.getRole().equals(Role.OFFICE_HEAD)) {
            if (getSignedPerson(permitToEnter, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            permitToEnter.setCurrentLocation(CurrentLocation.PRESIDENT);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(permitToEnter);
            sendMessageToOICWhenReceivesATransactionLetter(permitToEnter, Role.PRESIDENT);
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (getSignedPerson(permitToEnter, Role.OFFICE_HEAD).isEmpty())
                throw new ForbiddenException("The Office doesn't signed yet");

            permitToEnter.setStatus(LetterStatus.COMPLETED);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);

            // notify the moderator and student officer
            sendMessageToStudentOfficer(permitToEnter);
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }
        this.permitToEnterRepository.save(permitToEnter);
        this.signedPeopleRepository.save(signedPerson);
    }

    private void signatureForSFEF(String signature, int id, User user) {
        var sfef = this.sfefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SFEF not Found"));

        Optional<SignedPeople> optionalSignedPerson = sfef.getSignedPeople()
                .stream()
                .filter(sp -> sp.getRole().equals(user.getRole())).findFirst();

        if (signature == null || signature.isEmpty())
            throw new ForbiddenException("Please Attach your Signature First!");

        if (optionalSignedPerson.isEmpty()) throw new ForbiddenException("Invalid Assigned Officer for this Letter");

        var signedPerson = optionalSignedPerson.get();
        signedPerson.setSignature(signature);
        signedPerson.setUser(user);

        if (user.getRole().equals(Role.MODERATOR)) {
            sfef.setCurrentLocation(CurrentLocation.AUXILIARY_SERVICE_HEAD);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            sendMessageToStudentOfficer(sfef);
            sendMessageToOICWhenReceivesATransactionLetter(sfef, Role.AUXILIARY_SERVICE_HEAD);
        } else if (user.getRole().equals(Role.AUXILIARY_SERVICE_HEAD)) {
            if (getSignedPerson(sfef, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            sfef.setCurrentLocation(CurrentLocation.PPLO);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);

            sendMessageToStudentOfficer(sfef);
            sendMessageToOICWhenReceivesATransactionLetter(sfef, Role.PPLO);
        } else if (user.getRole().equals(Role.PPLO)) {
            if (getSignedPerson(sfef, Role.AUXILIARY_SERVICE_HEAD).isEmpty())
                throw new ForbiddenException("The Auxiliary doesn't signed yet");

            sfef.setCurrentLocation(CurrentLocation.PRESIDENT);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);

            sendMessageToStudentOfficer(sfef);
            sendMessageToOICWhenReceivesATransactionLetter(sfef, Role.PRESIDENT);
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (getSignedPerson(sfef, Role.MULTIMEDIA).isEmpty())
                throw new ForbiddenException("The Multimedia doesn't signed yet");

            sfef.setStatus(LetterStatus.COMPLETED);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);

            // notify the moderator and student officer
            sendMessageToStudentOfficer(sfef);
        } else {
            if (!user.getRole().equals(Role.CHAPEL) && !user.getRole().equals(Role.MULTIMEDIA))
                throw new ForbiddenException("You can't Perform to this action");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
        }
        this.sfefRepository.save(sfef);
        this.signedPeopleRepository.save(signedPerson);
    }

    @Override
    public void rejectLetter(TypeOfLetter type, int id, String reasonOfRejection) {
        switch (type) {
            case BUDGET_PROPOSAL:
                rejectBP(id, reasonOfRejection);
                break;
            case COMMUNICATION_LETTER:
                rejectCL(id, reasonOfRejection);
                break;
            case IMPLEMENTATION_LETTER_IN_CAMPUS:
                rejectILIC(id, reasonOfRejection);
                break;
            case IMPLEMENTATION_LETTER_OFF_CAMPUS:
                rejectILOC(id, reasonOfRejection);
                break;
            case PERMIT_TO_ENTER:
                rejectPTE(id, reasonOfRejection);
                break;
            case SFEF:
                rejectSFEF(id, reasonOfRejection);
                break;
            default:
                throw new ForbiddenException("Invalid Type of Letter");
        }
    }

    private void rejectPTE(int id, String reasonOfRejection) {
        var permitToEnter = this.permitToEnterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permit To Enter not Found"));

        var user = this.userUtil.getCurrentUser();
        var signedPerson = SignedPeople.builder()
                .permitToEnter(permitToEnter)
                .user(user)
                .status(SignedPeopleStatus.EVALUATED)
                .role(user.getRole())
                .build();
        this.signedPeopleRepository.save(signedPerson);

        permitToEnter.setStatus(LetterStatus.DECLINED);
        permitToEnter.setReasonOfRejection(reasonOfRejection);
        this.permitToEnterRepository.save(permitToEnter);

        // update student officer
        sendMessageToStudentOfficer(permitToEnter);
    }

    private void rejectSFEF(int id, String reasonOfRejection) {
        var sfef = this.sfefRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SFEF not Found"));

        var user = this.userUtil.getCurrentUser();
        var signedPerson = SignedPeople.builder()
                .sfef(sfef)
                .user(user)
                .status(SignedPeopleStatus.EVALUATED)
                .role(user.getRole())
                .build();
        this.signedPeopleRepository.save(signedPerson);

        sfef.setStatus(LetterStatus.DECLINED);
        sfef.setReasonOfRejection(reasonOfRejection);
        this.sfefRepository.save(sfef);

        // update student officer
        sendMessageToStudentOfficer(sfef);
    }

    @Override
    public List<GenericResponse> searchLetter(String query) {
        var user = this.userUtil.getCurrentUser();

        List<GenericResponse> genericResponses = new ArrayList<>();

        List<BudgetProposal> budgetProposals = this.budgetProposalRepository.findAllOIC(query, user.getId());
        budgetProposals.forEach(b -> genericResponses.add(transformToGeneric(b)));

        List<CommunicationLetter> communicationLetters = this.communicationLetterRepository.findAllOIC(query, user.getId());
        communicationLetters.forEach(c -> genericResponses.add(transformToGeneric(c, c)));

        List<ImplementationLetterInCampus> implementationLetterInCampuses = this.implementationLetterInCampusRepository.findAllOIC(query, user.getId());
        implementationLetterInCampuses.forEach(i -> genericResponses.add(transformToGeneric(i)));

        List<ImplementationLetterOffCampus> implementationLetterOffCampuses = this.implementationLetterOffCampusRepository.findAllOIC(query, user.getId());
        implementationLetterOffCampuses.forEach(i -> genericResponses.add(transformToGeneric(i)));

        // sort the gathered data
        genericResponses.sort(Comparator.comparing(GenericResponse::getCreatedDate).reversed());

        // Return a sublist for the specified range
        return genericResponses.subList(0, genericResponses.size());
    }

    private void rejectBP(int id, String reasonOfRejection) {
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget Proposal not Found"));

        var user = this.userUtil.getCurrentUser();

        var signedPerson = SignedPeople.builder()
                .budgetProposal(budgetProposal)
                .user(user)
                .status(SignedPeopleStatus.EVALUATED)
                .role(user.getRole())
                .build();
        this.signedPeopleRepository.save(signedPerson);

        budgetProposal.setStatus(LetterStatus.DECLINED);
        budgetProposal.setReasonOfRejection(reasonOfRejection);
        this.budgetProposalRepository.save(budgetProposal);

        // update student officer
        sendMessageToStudentOfficer(budgetProposal);
    }

    private void rejectCL(int id, String reasonOfRejection) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var signedPerson = SignedPeople.builder()
                .communicationLetter(communicationLetter)
                .user(user)
                .status(SignedPeopleStatus.EVALUATED)
                .role(user.getRole())
                .build();
        this.signedPeopleRepository.save(signedPerson);

        communicationLetter.setStatus(LetterStatus.DECLINED);
        communicationLetter.setReasonOfRejection(reasonOfRejection);
        this.communicationLetterRepository.save(communicationLetter);

        // update student officer
        sendMessageToStudentOfficer(communicationLetter);
    }

    private void rejectILIC(int id, String reasonOfRejection) {
        var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementation Letter not Found"));

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var signedPerson = SignedPeople.builder()
                .implementationLetterInCampus(implementationLetterInCampus)
                .user(user)
                .status(SignedPeopleStatus.EVALUATED)
                .role(user.getRole())
                .build();
        this.signedPeopleRepository.save(signedPerson);

        implementationLetterInCampus.setStatus(LetterStatus.DECLINED);
        implementationLetterInCampus.setReasonOfRejection(reasonOfRejection);
        this.implementationLetterInCampusRepository.save(implementationLetterInCampus);

        // update student officer
        sendMessageToStudentOfficer(implementationLetterInCampus);
    }

    private void rejectILOC(int id, String reasonOfRejection) {
        var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Implementation Letter not Found"));

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

        var signedPerson = SignedPeople.builder()
                .implementationLetterOffCampus(implementationLetterOffCampus)
                .user(user)
                .status(SignedPeopleStatus.EVALUATED)
                .role(user.getRole())
                .build();
        this.signedPeopleRepository.save(signedPerson);

        implementationLetterOffCampus.setStatus(LetterStatus.DECLINED);
        implementationLetterOffCampus.setReasonOfRejection(reasonOfRejection);
        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);

        // update student officer
        sendMessageToStudentOfficer(implementationLetterOffCampus);
    }

}

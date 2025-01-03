package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.UnauthorizedException;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposalMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposalRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterType;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampusMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampusRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampusMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampusRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleStatus;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserUtil;
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

    private final BudgetProposalMapper budgetProposalMapper;
    private final CommunicationLetterMapper communicationLetterMapper;
    private final ImplementationLetterInCampusMapper implementationLetterInCampusMapper;
    private final ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper;

    private final SignedPeopleMapper signedPeopleMapper;

    private final UserUtil userUtil;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;
    private final SignedPeopleRepository signedPeopleRepository;

    public GenericLetterServiceImp(BudgetProposalRepository budgetProposalRepository, ImplementationLetterInCampusRepository implementationLetterInCampusRepository, ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository, CommunicationLetterRepository communicationLetterRepository, BudgetProposalMapper budgetProposalMapper, CommunicationLetterMapper communicationLetterMapper, ImplementationLetterInCampusMapper implementationLetterInCampusMapper, ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper, SignedPeopleMapper signedPeopleMapper, UserUtil userUtil, DateTimeFormatterUtil dateTimeFormatterUtil, SignedPeopleRepository signedPeopleRepository) {
        this.budgetProposalRepository = budgetProposalRepository;
        this.implementationLetterInCampusRepository = implementationLetterInCampusRepository;
        this.implementationLetterOffCampusRepository = implementationLetterOffCampusRepository;
        this.communicationLetterRepository = communicationLetterRepository;
        this.budgetProposalMapper = budgetProposalMapper;
        this.communicationLetterMapper = communicationLetterMapper;
        this.implementationLetterInCampusMapper = implementationLetterInCampusMapper;
        this.implementationLetterOffCampusMapper = implementationLetterOffCampusMapper;
        this.signedPeopleMapper = signedPeopleMapper;
        this.userUtil = userUtil;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
        this.signedPeopleRepository = signedPeopleRepository;
    }

    @Override
    public List<GenericResponse> getAllLetters(int s) {
        if (s > 50) throw new ForbiddenException("Maximum size exceeded");

        List<GenericResponse> genericResponses = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 50);

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Invalid User");

        if (user.getRole().equals(Role.MODERATOR) || user.getRole().equals(Role.STUDENT_OFFICER)) {
            Page<BudgetProposal> budgetProposalPage = this.budgetProposalRepository.findAll(pageable, user.getId(), user.getRole().name());
            budgetProposalPage.getContent().forEach(b -> genericResponses.add(transformToGeneric(b)));

            Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(pageable, user.getId(), user.getRole().name());
            communicationLetterPage.getContent().forEach(c -> genericResponses.add(transformToGeneric(c, c)));

            Page<ImplementationLetterInCampus> implementationLetterInCampusPage = this.implementationLetterInCampusRepository.findAll(pageable, user.getId(), user.getRole().name());
            implementationLetterInCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

            Page<ImplementationLetterOffCampus> implementationLetterOffCampusPage = this.implementationLetterOffCampusRepository.findAll(pageable, user.getId(), user.getRole().name());
            implementationLetterOffCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));
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
            // Sort the responses by a common property, e.g., "createdDate"
            genericResponses.sort(Comparator.comparing(GenericResponse::getCreatedDate).reversed());
        }

        // sort the gathered data
        genericResponses.sort(Comparator.comparing(GenericResponse::getCreatedDate).reversed());

        // Return a sublist for the specified range
        return genericResponses.subList(0, Math.min(s, genericResponses.size()));
    }

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
            default:
                throw new ForbiddenException("Invalid Type of Letter");
        }
    }

    private void budgetProposalOnClick(int id) {
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Budget Proposal"));

        if (budgetProposal.getStatus().equals(LetterStatus.COMPLETED)
                || budgetProposal.getStatus().equals(LetterStatus.DECLINED)) return;

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

        budgetProposal.setStatus(LetterStatus.IN_PROGRESS);
        this.budgetProposalRepository.save(budgetProposal);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void communicationLetterOnClick(int id) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Invalid Communication Letter"));

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

        communicationLetter.setStatus(LetterStatus.IN_PROGRESS);
        this.communicationLetterRepository.save(communicationLetter);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void implementationLetterInCampusOnClick(int id) {
        var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Implementation Letter In Campus"));

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

        implementationLetterInCampus.setStatus(LetterStatus.IN_PROGRESS);
        this.implementationLetterInCampusRepository.save(implementationLetterInCampus);
        this.signedPeopleRepository.save(tempSignedPerson);
    }

    private void implementationLetterOffCampusOnClick(int id) {
        var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Implementation Letter Off Campus"));

        if (implementationLetterOffCampus.getStatus().equals(LetterStatus.COMPLETED) ||
                implementationLetterOffCampus.getStatus().equals(LetterStatus.DECLINED)) return;

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

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

        implementationLetterOffCampus.setStatus(LetterStatus.IN_PROGRESS);
        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);
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
            budgetProposal.setCurrentLocation(CurrentLocation.DSA);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            // check if the user has already been signed
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(budgetProposal, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            budgetProposal.setCurrentLocation(CurrentLocation.FINANCE);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);

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
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }

        this.budgetProposalRepository.save(budgetProposal);
        this.signedPeopleRepository.save(signedPerson);
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
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(communicationLetter, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            if (communicationLetter.getTypeOfCampus().equals(CommunicationLetterType.IN_CAMPUS)) {
                communicationLetter.setCurrentLocation(CurrentLocation.PRESIDENT);
            } else {
                communicationLetter.setCurrentLocation(CurrentLocation.OFFICE_HEAD);
            }
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
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
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(implementationLetterInCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(implementationLetterInCampus, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            implementationLetterInCampus.setStatus(LetterStatus.COMPLETED);
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

        if (user.getRole().equals(Role.COMMUNITY)) {
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            implementationLetterOffCampus.setCurrentLocation(CurrentLocation.PRESIDENT);
            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");

            if (getSignedPerson(implementationLetterOffCampus, Role.COMMUNITY).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            signedPerson.setStatus(SignedPeopleStatus.EVALUATED);
            implementationLetterOffCampus.setStatus(LetterStatus.COMPLETED);
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }
        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);
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
            default:
                throw new ForbiddenException("Invalid Type of Letter");
        }
    }

    private void rejectBP(int id, String reasonOfRejection) {
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget Proposal not Found"));

        var user = this.userUtil.getCurrentUser();
        if (user == null) throw new UnauthorizedException("Session Expired");

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
    }

}

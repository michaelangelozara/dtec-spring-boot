package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
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
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeopleRepository;
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

    private final UserUtil userUtil;

    private final SignedPeopleRepository signedPeopleRepository;

    public GenericLetterServiceImp(BudgetProposalRepository budgetProposalRepository, ImplementationLetterInCampusRepository implementationLetterInCampusRepository, ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository, CommunicationLetterRepository communicationLetterRepository, BudgetProposalMapper budgetProposalMapper, CommunicationLetterMapper communicationLetterMapper, ImplementationLetterInCampusMapper implementationLetterInCampusMapper, ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper, UserUtil userUtil, SignedPeopleRepository signedPeopleRepository) {
        this.budgetProposalRepository = budgetProposalRepository;
        this.implementationLetterInCampusRepository = implementationLetterInCampusRepository;
        this.implementationLetterOffCampusRepository = implementationLetterOffCampusRepository;
        this.communicationLetterRepository = communicationLetterRepository;
        this.budgetProposalMapper = budgetProposalMapper;
        this.communicationLetterMapper = communicationLetterMapper;
        this.implementationLetterInCampusMapper = implementationLetterInCampusMapper;
        this.implementationLetterOffCampusMapper = implementationLetterOffCampusMapper;
        this.userUtil = userUtil;
        this.signedPeopleRepository = signedPeopleRepository;
    }

    @Override
    public List<GenericResponse> getAllLetters(LetterStatus status) {
        List<GenericResponse> genericResponses = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 5);

        Page<BudgetProposal> budgetProposalPage = this.budgetProposalRepository.findAll(status, pageable);
        budgetProposalPage.getContent().forEach(b -> genericResponses.add(transformToGeneric(b)));

        Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(status, pageable);
        communicationLetterPage.getContent().forEach(c -> genericResponses.add(transformToGeneric(c, c)));

        Page<ImplementationLetterInCampus> implementationLetterInCampusPage = this.implementationLetterInCampusRepository.findAll(status, pageable);
        implementationLetterInCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));

        Page<ImplementationLetterOffCampus> implementationLetterOffCampusPage = this.implementationLetterOffCampusRepository.findAll(status, pageable);
        implementationLetterOffCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i)));
        return genericResponses;
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

        budgetProposal.setStatus(LetterStatus.IN_PROGRESS);
        this.budgetProposalRepository.save(budgetProposal);
    }

    private void communicationLetterOnClick(int id) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Invalid Communication Letter"));

        communicationLetter.setStatus(LetterStatus.IN_PROGRESS);
        this.communicationLetterRepository.save(communicationLetter);
    }

    private void implementationLetterInCampusOnClick(int id) {
        var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Implementation Letter In Campus"));

        implementationLetterInCampus.setStatus(LetterStatus.IN_PROGRESS);
        this.implementationLetterInCampusRepository.save(implementationLetterInCampus);
    }

    private void implementationLetterOffCampusOnClick(int id) {
        var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Implementation Letter Off Campus"));

        implementationLetterOffCampus.setStatus(LetterStatus.IN_PROGRESS);
        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);
    }

    private GenericResponse transformToGeneric(SharedFields object) {

        Map<String, Object> fields = new HashMap<>();
        fields.put("date_requested", object.getCreatedAt());
        fields.put("letter_type", object.getType());
        fields.put("name_of_transaction", object.getNameOfTransaction());
        fields.put("requested_by", object.getClub().getName());
        fields.put("status", object.getStatus());
        fields.put("last_update", object.getLastModified());

        return GenericResponse.builder()
                .id(object.getId())
                .type(object.getType())
                .fields(fields)
                .build();
    }

    private GenericResponse transformToGeneric(SharedFields object, Object cml) {

        Map<String, Object> fields = new HashMap<>();
        fields.put("date_requested", object.getCreatedAt());
        fields.put("letter_type", object.getType());
        fields.put("name_of_transaction", object.getNameOfTransaction());
        fields.put("requested_by", object.getClub().getName());
        fields.put("status", object.getStatus());
        fields.put("last_update", object.getLastModified());

        return GenericResponse.builder()
                .id(object.getId())
                .type(object.getType())
                .fields(fields)
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

                // check if the letter's signatures are completed
                var budgetProposal = this.budgetProposalRepository.findById(letterId)
                        .orElse(null);

                if (budgetProposal != null) {
                    if (budgetProposal.getSignedPeople().size() >= 5) {
                        budgetProposal.setStatus(LetterStatus.COMPLETED);
                        this.budgetProposalRepository.save(budgetProposal);
                    }
                }
                break;
            case COMMUNICATION_LETTER:
                signatureForCLLetter(signature, letterId, user);

                // check if the letter's signatures are completed
                var communicationLetter = this.communicationLetterRepository.findById(letterId)
                        .orElse(null);
                if (communicationLetter != null) {
                    if (communicationLetter.getTypeOfCampus().equals(CommunicationLetterType.IN_CAMPUS)
                            && communicationLetter.getSignedPeople().size() >= 4) {
                        communicationLetter.setStatus(LetterStatus.COMPLETED);
                        this.communicationLetterRepository.save(communicationLetter);
                    } else if (communicationLetter.getSignedPeople().size() >= 3) {
                        communicationLetter.setStatus(LetterStatus.COMPLETED);
                        this.communicationLetterRepository.save(communicationLetter);
                    }
                }
                break;
            case IMPLEMENTATION_LETTER_IN_CAMPUS:
                signatureForILICLetter(signature, letterId, user);

                // check if the letter's signatures are completed
                var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(letterId)
                        .orElse(null);
                if(implementationLetterInCampus != null){
                    if(implementationLetterInCampus.getSignedPeople().size() >= 3){
                        implementationLetterInCampus.setStatus(LetterStatus.COMPLETED);
                        this.implementationLetterInCampusRepository.save(implementationLetterInCampus);
                    }
                }
                break;
            case IMPLEMENTATION_LETTER_OFF_CAMPUS:
                signatureForILOCLetter(signature, letterId, user);

                // check if the letter's signatures are completed
                var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(letterId)
                        .orElse(null);
                if(implementationLetterOffCampus != null){
                    if(implementationLetterOffCampus.getSignedPeople().size() >= 4){
                        implementationLetterOffCampus.setStatus(LetterStatus.COMPLETED);
                        this.implementationLetterOffCampusRepository.save(implementationLetterOffCampus);
                    }
                }
                break;
            default:
                throw new ForbiddenException("Invalid Type of Letter");
        }
    }

    private void signatureForBPLetter(String signature, int id, User user) {
        var budgetProposal = this.budgetProposalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget Proposal not Found"));

        var signedPerson = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(signature)
                .budgetProposal(budgetProposal)
                .build();

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the student has already been signed
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(budgetProposal, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");
        } else if (user.getRole().equals(Role.FINANCE)) {
            if (getSignedPerson(budgetProposal, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(budgetProposal, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");

            if (getSignedPerson(budgetProposal, Role.DSA).isEmpty())
                throw new ForbiddenException("The DSA doesn't signed yet");

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
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }

        this.signedPeopleRepository.save(signedPerson);
    }

    private Optional<SignedPeople> getSignedPerson(SharedFields object, Role role) {
        return object.getSignedPeople()
                .stream().filter(s -> s.getRole().equals(role)).findFirst();
    }

    private void signatureForCLLetter(String signature, int id, User user) {
        var communicationLetter = this.communicationLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        var signedPerson = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(signature)
                .communicationLetter(communicationLetter)
                .build();

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the student has already been signed
            if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(communicationLetter, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            // this only applicable to communication letter in campus
            if (communicationLetter.getTypeOfCampus().equals(CommunicationLetterType.IN_CAMPUS)) {
                if (getSignedPerson(communicationLetter, Role.STUDENT_OFFICER).isEmpty())
                    throw new ForbiddenException("The Student Officer doesn't signed yet");
                // check if the moderator has already been signed
                if (getSignedPerson(communicationLetter, Role.MODERATOR).isEmpty())
                    throw new ForbiddenException("The Moderator doesn't signed yet");

                if (getSignedPerson(communicationLetter, Role.DSA).isEmpty())
                    throw new ForbiddenException("The DSA doesn't signed yet");
            }
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }

        this.signedPeopleRepository.save(signedPerson);
    }

    private void signatureForILICLetter(String signature, int id, User user) {
        var implementationLetterInCampus = this.implementationLetterInCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        var signedPerson = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(signature)
                .implementationLetterInCampus(implementationLetterInCampus)
                .build();

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the student has already been signed
            if (getSignedPerson(implementationLetterInCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
        } else if (user.getRole().equals(Role.DSA)) {
            if (getSignedPerson(implementationLetterInCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(implementationLetterInCampus, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }

        this.signedPeopleRepository.save(signedPerson);
    }

    private void signatureForILOCLetter(String signature, int id, User user) {
        var implementationLetterOffCampus = this.implementationLetterOffCampusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Communication Letter not Found"));

        var signedPerson = SignedPeople.builder()
                .user(user)
                .role(user.getRole())
                .signature(signature)
                .implementationLetterOffCampus(implementationLetterOffCampus)
                .build();

        if (user.getRole().equals(Role.MODERATOR)) {
            // check if the student has already been signed
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
        } else if (user.getRole().equals(Role.COMMUNITY)) {
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(implementationLetterOffCampus, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");
        } else if (user.getRole().equals(Role.PRESIDENT)) {
            if (getSignedPerson(implementationLetterOffCampus, Role.STUDENT_OFFICER).isEmpty())
                throw new ForbiddenException("The Student Officer doesn't signed yet");
            // check if the moderator has already been signed
            if (getSignedPerson(implementationLetterOffCampus, Role.MODERATOR).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");
            if (getSignedPerson(implementationLetterOffCampus, Role.COMMUNITY).isEmpty())
                throw new ForbiddenException("The Moderator doesn't signed yet");
        } else {
            throw new ForbiddenException("You can't Perform to this action");
        }

        this.signedPeopleRepository.save(signedPerson);
    }

}

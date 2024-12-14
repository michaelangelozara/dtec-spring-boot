package com.DTEC.Document_Tracking_and_E_Clearance.letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposalMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposalRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter.CommunicationLetterRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampusMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampusRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampus;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampusMapper;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus.ImplementationLetterOffCampusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public GenericLetterServiceImp(BudgetProposalRepository budgetProposalRepository, ImplementationLetterInCampusRepository implementationLetterInCampusRepository, ImplementationLetterOffCampusRepository implementationLetterOffCampusRepository, CommunicationLetterRepository communicationLetterRepository, BudgetProposalMapper budgetProposalMapper, CommunicationLetterMapper communicationLetterMapper, ImplementationLetterInCampusMapper implementationLetterInCampusMapper, ImplementationLetterOffCampusMapper implementationLetterOffCampusMapper) {
        this.budgetProposalRepository = budgetProposalRepository;
        this.implementationLetterInCampusRepository = implementationLetterInCampusRepository;
        this.implementationLetterOffCampusRepository = implementationLetterOffCampusRepository;
        this.communicationLetterRepository = communicationLetterRepository;
        this.budgetProposalMapper = budgetProposalMapper;
        this.communicationLetterMapper = communicationLetterMapper;
        this.implementationLetterInCampusMapper = implementationLetterInCampusMapper;
        this.implementationLetterOffCampusMapper = implementationLetterOffCampusMapper;
    }

    @Override
    public List<GenericResponse> getAllLetters(LetterStatus status) {
        List<GenericResponse> genericResponses = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 5);

        Page<BudgetProposal> budgetProposalPage = this.budgetProposalRepository.findAll(status, pageable);
        budgetProposalPage.getContent().forEach(b -> genericResponses.add(transformToGeneric(b, this.budgetProposalMapper.toBudgetProposalInformationResponseDto(b))));

        Page<CommunicationLetter> communicationLetterPage = this.communicationLetterRepository.findAll(status, pageable);
        communicationLetterPage.getContent().forEach(c -> genericResponses.add(transformToGeneric(c, this.communicationLetterMapper.toCommunicationLetterResponseDto(c))));

        Page<ImplementationLetterInCampus> implementationLetterInCampusPage = this.implementationLetterInCampusRepository.findAll(status, pageable);
        implementationLetterInCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i, this.implementationLetterInCampusMapper.toImplementationLetterInCampusResponseDto(i))));

        Page<ImplementationLetterOffCampus> implementationLetterOffCampusPage = this.implementationLetterOffCampusRepository.findAll(status, pageable);
        implementationLetterOffCampusPage.getContent().forEach(i -> genericResponses.add(transformToGeneric(i, this.implementationLetterOffCampusMapper.toImplementationLetterOffCampusResponseDto(i))));
        return genericResponses;
    }

    private GenericResponse transformToGeneric(SharedFields object, Object o) {

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
                .object(o)
                .build();
    }
}

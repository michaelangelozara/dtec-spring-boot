package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance_signoff.ClearanceSignoffRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.CodeGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.SchoolYearGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClearanceServiceImp implements ClearanceService {

    private final String prefix = "CLR-";

    private final UserRepository userRepository;
    private final ClearanceSignoffRepository clearanceSignoffRepository;
    private final ClearanceRepository clearanceRepository;
    private final ClearanceMapper clearanceMapper;
    private final CodeGenerator codeGenerator;
    private final SchoolYearGenerator schoolYearGenerator;

    public ClearanceServiceImp(UserRepository userRepository, ClearanceSignoffRepository clearanceSignoffRepository, ClearanceRepository clearanceRepository, ClearanceMapper clearanceMapper, CodeGenerator codeGenerator, SchoolYearGenerator schoolYearGenerator) {
        this.userRepository = userRepository;
        this.clearanceSignoffRepository = clearanceSignoffRepository;
        this.clearanceRepository = clearanceRepository;
        this.clearanceMapper = clearanceMapper;
        this.codeGenerator = codeGenerator;
        this.schoolYearGenerator = schoolYearGenerator;
    }

    @Transactional
    @Override
    public String releaseClearances() {
        var students = this.userRepository.findAllStudents();
        if (students.isEmpty())
            throw new ResourceNotFoundException("No Registered Student yet");

        // count of row
        int count = this.clearanceRepository.countRow();

        List<Clearance> clearances = new ArrayList<>();
        for (var student : students) {
            count++;
            var clearance = this.clearanceMapper.toClearance(this.schoolYearGenerator.generateSchoolYear());
            clearance.setStudent(student);
            clearance.setClearanceCode(this.codeGenerator.generateCode(prefix, count));
            clearance.setSchoolYear(this.schoolYearGenerator.generateSchoolYear());
            clearances.add(clearance);
        }
        this.clearanceRepository.saveAll(clearances);
        return "Clearances Released";
    }

    @Override
    public String generateClearanceByUserId(int id) {
        var student = this.userRepository.findById(id).orElse(null);
        if (student == null)
            throw new ResourceNotFoundException("Student Not Found");

        var clearance = this.clearanceMapper.toClearance(this.schoolYearGenerator.generateSchoolYear());
        clearance.setStudent(student);
        clearance.setClearanceCode(this.codeGenerator.generateCode(prefix, this.clearanceRepository.countRow() + 1));
        this.clearanceRepository.save(clearance);
        return "Clearance Generated for " + student.getLastname();
    }

    @Override
    public List<ClearanceResponseDto> getAllClearances(int n) {
        Pageable pageable = PageRequest.of(0, n);
        Page<Clearance> clearances = this.clearanceRepository.findAll(pageable);
        return this.clearanceMapper.toClearanceResponseDtoList(clearances.getContent());
    }

    @Override
    public ClearanceResponseDto getClearanceByStudentId(int id) {
        Optional<Clearance> clearanceOptional = this.clearanceRepository.findClearanceByUserId(id);
        if (clearanceOptional.isEmpty())
            throw new ResourceNotFoundException("Clearance not Found");

        return this.clearanceMapper.toClearanceResponseDto(clearanceOptional.get());
    }

    @Override
    public String signClearance(SignClearanceRequestDto dto) {
        var clearance = this.clearanceRepository.findById(dto.clearanceId())
                .orElse(null);
        if (clearance == null)
            throw new ResourceNotFoundException("Clearance not Found");

        var personnel = this.userRepository.findById(dto.userId()).orElse(null);
        if (personnel == null)
            throw new ResourceNotFoundException("User not Found");

        // check if the user has already signed
        boolean isPersonnelSigned = false;
        ClearanceSignoff updatedSignoff = null;
        for (var signoff : clearance.getClearanceSignoffs()) {
            if (signoff.getPersonnel().getId().equals(personnel.getId())) {
                signoff.setSignature(personnel.getSignature());
                isPersonnelSigned = true;
                updatedSignoff = signoff;
                break;
            }
        }

        if (!isPersonnelSigned) {
            ClearanceSignoff clearanceSignoff = new ClearanceSignoff();
            clearanceSignoff.setClearance(clearance);
            clearanceSignoff.setPersonnel(personnel);
            clearanceSignoff.setSignature(personnel.getSignature());
            clearanceSignoff.setType(dto.type());

            this.clearanceRepository.save(clearance);
        } else {
            this.clearanceSignoffRepository.save(updatedSignoff);
        }

        // update the clearance
        return isPersonnelSigned ? "Signature is Successfully Updated" : "Signature is Successfully Added";
    }
}

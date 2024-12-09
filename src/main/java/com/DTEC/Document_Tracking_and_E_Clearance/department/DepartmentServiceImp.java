package com.DTEC.Document_Tracking_and_E_Clearance.department;

import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.NoContentException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.CodeGenerator;
import com.DTEC.Document_Tracking_and_E_Clearance.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImp implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final UserRepository userRepository;

    public DepartmentServiceImp(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.userRepository = userRepository;
    }

    @Override
    public DepartmentResponseDto getDepartmentById(int id) {
        var department = this.departmentRepository.findById(id).orElse(null);
        if (department == null)
            throw new ResourceNotFoundException("Department not Found");

        return this.departmentMapper.toDepartmentResponseDto(department);
    }

    @Override
    public List<DepartmentResponseDto> getAllDepartments() {
        var departments = this.departmentRepository.findAll();
        if (departments.isEmpty())
            throw new NoContentException("No Added Department yet");

        return this.departmentMapper.departmentResponseDtoList(departments);
    }

    @Override
    public String addDepartment(AddDepartmentRequestDto dto) {
        if (dto.name().isEmpty())
            throw new ForbiddenException("Department Name cannot be Blank");

        var department = this.departmentMapper.toDepartment(dto);
        department.setDepartmentCode(CodeGenerator.generateCode("DEP-", this.departmentRepository.countRow()));
        this.departmentRepository.save(department);

        return "Department Successfully Added";
    }
}

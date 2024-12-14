package com.DTEC.Document_Tracking_and_E_Clearance.department;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public List<DepartmentResponseDto> getAllDepartment(){
        var departments = this.departmentRepository.findAll();
        return this.departmentMapper.toDepartmentResponseDtoList(departments);
    }
}

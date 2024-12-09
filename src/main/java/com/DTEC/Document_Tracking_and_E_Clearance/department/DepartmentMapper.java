package com.DTEC.Document_Tracking_and_E_Clearance.department;

import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class DepartmentMapper {

    public Department toDepartment(AddDepartmentRequestDto dto){
        return Department.builder()
                .name(dto.name())
                .logo(Base64.getEncoder().encodeToString(dto.logo()))
                .build();
    }

    public DepartmentResponseDto toDepartmentResponseDto(Department department) {
        return new DepartmentResponseDto(
                department.getId(),
                department.getName(),
                department.getLogo(),
                department.getCreatedAt(),
                department.getLastModified()
        );
    }

    public List<DepartmentResponseDto> departmentResponseDtoList(List<Department> departments){
        return departments
                .stream()
                .map(this::toDepartmentResponseDto)
                .toList();
    }
}

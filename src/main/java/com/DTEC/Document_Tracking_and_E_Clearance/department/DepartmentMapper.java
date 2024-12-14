package com.DTEC.Document_Tracking_and_E_Clearance.department;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentMapper {

    public DepartmentResponseDto toDepartmentResponseDto(Department department){
        return new DepartmentResponseDto(
                department.getId(),
                department.getName(),
                department.getCreatedAt()
        );
    }

    public List<DepartmentResponseDto> toDepartmentResponseDtoList(List<Department> departments){
        return departments
                .stream()
                .map(this::toDepartmentResponseDto)
                .toList();
    }
}

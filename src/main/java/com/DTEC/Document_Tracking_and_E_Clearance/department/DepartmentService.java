package com.DTEC.Document_Tracking_and_E_Clearance.department;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDto getDepartmentById(int id);

    List<DepartmentResponseDto> getAllDepartments();

    String addDepartment(AddDepartmentRequestDto dto);
}

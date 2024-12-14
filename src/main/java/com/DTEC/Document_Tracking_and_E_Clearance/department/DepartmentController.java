package com.DTEC.Document_Tracking_and_E_Clearance.department;

import com.DTEC.Document_Tracking_and_E_Clearance.misc.DateTimeFormatterUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DateTimeFormatterUtil dateTimeFormatterUtil;

    public DepartmentController(DepartmentService departmentService, DateTimeFormatterUtil dateTimeFormatterUtil) {
        this.departmentService = departmentService;
        this.dateTimeFormatterUtil = dateTimeFormatterUtil;
    }


}

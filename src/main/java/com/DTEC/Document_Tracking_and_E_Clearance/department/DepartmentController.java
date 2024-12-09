package com.DTEC.Document_Tracking_and_E_Clearance.department;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDto>> getAllDepartment(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDto> getDepartment(
            @PathVariable("id") int id
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.departmentService.getDepartmentById(id));
    }

    @PostMapping("/add-department")
    public ResponseEntity<String> addDepartment(@RequestBody AddDepartmentRequestDto dto){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.departmentService.addDepartment(dto));
    }
}

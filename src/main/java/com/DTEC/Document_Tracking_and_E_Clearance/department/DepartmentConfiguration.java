package com.DTEC.Document_Tracking_and_E_Clearance.department;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DepartmentConfiguration {

    private final DepartmentRepository departmentRepository;

    public DepartmentConfiguration(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Bean(name = "department-initializer")
    @Order(1)
    public ApplicationRunner applicationRunner() {
        return args -> {
            int count = this.departmentRepository.countRow();
            if (count == 0) {
                Department d1 = Department.builder()
                        .name("College of Arts and Sciences, and Education")
                        .shortName("CASED")
                        .departmentCode("DEP-001")
                        .createdAt(LocalDate.now())
                        .build();

                Department d2 = Department.builder()
                        .name("College of Business and Technical Vocational Courses")
                        .shortName("CBTV")
                        .departmentCode("DEP-002")
                        .createdAt(LocalDate.now())
                        .build();

                Department d3 = Department.builder()
                        .name("College of Nursing")
                        .shortName("CN")
                        .departmentCode("DEP-003")
                        .createdAt(LocalDate.now())
                        .build();
                this.departmentRepository.saveAll(List.of(d1, d2, d3));
            }
        };
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.course;

import com.DTEC.Document_Tracking_and_E_Clearance.department.Department;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseConfiguration {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public CourseConfiguration(CourseRepository courseRepository, DepartmentRepository departmentRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
    }


    @Order(2)
    @Bean(name = "course-initializer")
    public ApplicationRunner applicationRunner() {
        return args -> {
            var departments = this.departmentRepository.findAll();

            int count = this.courseRepository.countRow();
            if (count == 0 && !departments.isEmpty()) {
                List<String[]> coursesNames = new ArrayList<>();
                coursesNames.add(new String[]{"Bachelor of Arts in Political Science (AB)", "AB", "1"});
                coursesNames.add(new String[]{"Bachelor of Elementary Education (BEEd)", "BEEd", "1"});
                coursesNames.add(new String[]{"Bachelor of Science in Computer Engineering (BSCpE)", "BSCpE", "1"});
                coursesNames.add(new String[]{"Bachelor of Science in Computer Science (BSCS)", "BSCS", "1"});
                coursesNames.add(new String[]{"Bachelor of Science in Criminology (BSCrim)", "BSCrim", "1"});
                coursesNames.add(new String[]{"Bachelor of Science in Social Work (BSSW)", "BSSW", "1"});
                coursesNames.add(new String[]{"Bachelor of Secondary Education (BSEd) Major in English", "BSEd", "1"});
                coursesNames.add(new String[]{"Bachelor of Secondary Education (BSEd) Major in Mathematics", "BSEd", "1"});

                coursesNames.add(new String[]{"Bachelor of Science in Accountancy (BSA)", "BSA", "2"});
                coursesNames.add(new String[]{"Bachelor of Science in Business Administration (BSBA) Major in Financial Management", "BSBA", "2"});
                coursesNames.add(new String[]{"Bachelor of Science in Business Administration (BSBA) Major in Marketing Management", "BSBA", "2"});
                coursesNames.add(new String[]{"Bachelor of Science in Hospitality Management (BSHM)", "BSHM", "2"});
                coursesNames.add(new String[]{"TESDA Programs - Cookery NC II", "N/A", "2"});
                coursesNames.add(new String[]{"TESDA Programs - Food and Beverages NC II", "N/A", "2"});
                coursesNames.add(new String[]{"TESDA Programs - Housekeeping NC II", "N/A", "2"});

                coursesNames.add(new String[]{"Bachelor of Science in Nursing (BSN)", "BSN", "3"});

                List<Course> courses = new ArrayList<>();
                for (var courseName : coursesNames) {
                    Integer depId = Integer.parseInt(courseName[2]);
                    for (var department : departments) {
                        if (depId.equals(department.getId())) {
                            var course = toCourse(courseName[0], courseName[1], department);
                            courses.add(course);
                            break;
                        }
                    }
                }

                this.courseRepository.saveAll(courses);
            }
        };
    }

    private Course toCourse(String name, String shortName, Department department) {
        return Course.builder()
                .name(name)
                .shortName(shortName)
                .department(department)
                .build();
    }
}

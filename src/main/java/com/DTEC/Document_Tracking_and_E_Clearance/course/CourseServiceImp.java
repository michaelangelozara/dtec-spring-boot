package com.DTEC.Document_Tracking_and_E_Clearance.course;

import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ResourceNotFoundException;
import com.DTEC.Document_Tracking_and_E_Clearance.misc.CodeGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImp implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;
    private final CodeGenerator codeGenerator;

    public CourseServiceImp(CourseRepository courseRepository, CourseMapper courseMapper, ClubRepository clubRepository, DepartmentRepository departmentRepository, CodeGenerator codeGenerator) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.clubRepository = clubRepository;
        this.departmentRepository = departmentRepository;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public List<CourseResponseDto> getAllCourse() {
        var courses = this.courseRepository.findAll();
        if (courses.isEmpty())
            throw new ResourceNotFoundException("No Added Course yet");

        return this.courseMapper.courseResponseDtoList(courses);
    }

    @Override
    public CourseResponseDto getCourseById(int id) {
        var course = this.courseRepository.findById(id).orElse(null);
        if (course == null)
            throw new ResourceNotFoundException("Course not Found");

        return this.courseMapper.toCourseResponseDto(course);
    }
}

package com.DTEC.Document_Tracking_and_E_Clearance.course;

import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.department.DepartmentRepository;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.ForbiddenException;
import com.DTEC.Document_Tracking_and_E_Clearance.exception.NoContentException;
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

    public CourseServiceImp(CourseRepository courseRepository, CourseMapper courseMapper, ClubRepository clubRepository, DepartmentRepository departmentRepository) {
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
        this.clubRepository = clubRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public String addCourse(AddCourseRequestDto dto) {
        var department = this.departmentRepository.findById(dto.departmentId())
                .orElse(null);
        if (department == null)
            throw new ResourceNotFoundException("Department not Found");

        var departmentClub = this.clubRepository.findById(dto.departmentClubId())
                .orElse(null);
        if (departmentClub == null)
            throw new ResourceNotFoundException("Department Club not Found");

        if (dto.name().isEmpty())
            throw new ForbiddenException("Empty Name is not Allowed");

        var course = new Course();
        course.setName(dto.name());
        course.setCourseCode(CodeGenerator.generateCode("CRS-", this.courseRepository.countRow()));
        course.setDepartment(department);
        course.setDepartmentClub(departmentClub);

        this.courseRepository.save(course);
        return "Course Successfully Added";
    }

    @Override
    public List<CourseResponseDto> getAllCourse() {
        var courses = this.courseRepository.findAll();
        if (courses.isEmpty())
            throw new NoContentException("No Added Course yet");

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

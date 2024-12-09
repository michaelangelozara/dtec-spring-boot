package com.DTEC.Document_Tracking_and_E_Clearance.course;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMapper {

    public Course toCourse(AddCourseRequestDto dto){
        return Course.builder()
                .name(dto.name())
                .build();
    }

    public CourseResponseDto toCourseResponseDto(Course course){
        return new CourseResponseDto(
                course.getId(),
                course.getName(),
                course.getCreatedAt(),
                course.getLastModified()
        );
    }

    public List<CourseResponseDto> courseResponseDtoList(List<Course> courses){
        return courses
                .stream()
                .map(this::toCourseResponseDto)
                .toList();
    }
}

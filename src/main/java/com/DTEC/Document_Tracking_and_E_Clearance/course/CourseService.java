package com.DTEC.Document_Tracking_and_E_Clearance.course;

import java.util.List;

public interface CourseService {

    String addCourse(AddCourseRequestDto dto);

    List<CourseResponseDto> getAllCourse();

    CourseResponseDto getCourseById(int id);
}

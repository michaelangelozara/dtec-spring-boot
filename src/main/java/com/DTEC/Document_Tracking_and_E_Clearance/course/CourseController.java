package com.DTEC.Document_Tracking_and_E_Clearance.course;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        return ResponseEntity.status(HttpStatus.OK).body(this.courseService.getAllCourse());
    }

    @GetMapping("/{course-id}")
    public ResponseEntity<CourseResponseDto> getCourse(
            @PathVariable("course-id") int id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(this.courseService.getCourseById(id));
    }

    @PostMapping("/add-course")
    public ResponseEntity<String> addCourse(
            @RequestBody AddCourseRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.courseService.addCourse(dto));
    }
}

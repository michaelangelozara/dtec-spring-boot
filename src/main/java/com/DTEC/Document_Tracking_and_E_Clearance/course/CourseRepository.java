package com.DTEC.Document_Tracking_and_E_Clearance.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query("SELECT COUNT(c) FROM Course c")
    Integer countRow();
}

package com.DTEC.Document_Tracking_and_E_Clearance.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("SELECT COUNT(d) FROM Department d")
    Integer countRow();
}

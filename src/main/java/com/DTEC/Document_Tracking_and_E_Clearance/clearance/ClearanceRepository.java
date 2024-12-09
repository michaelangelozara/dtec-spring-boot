package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import org.hibernate.annotations.HQLSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClearanceRepository extends JpaRepository<Clearance, Integer> {

    @Query("SELECT c FROM Clearance c WHERE c.student.id = :id")
    Optional<Clearance> findClearanceByUserId(@Param("id") int id);

    @Query("SELECT COUNT(c) FROM Clearance c")
    Integer countRow();
}

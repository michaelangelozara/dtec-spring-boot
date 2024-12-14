package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImplementationLetterOffCampusRepository extends JpaRepository<ImplementationLetterOffCampus, Integer> {

    @Query("SELECT i FROM ImplementationLetterOffCampus i WHERE i.status =:status")
    Page<ImplementationLetterOffCampus> findAll(@Param("status") LetterStatus status, Pageable pageable);
}

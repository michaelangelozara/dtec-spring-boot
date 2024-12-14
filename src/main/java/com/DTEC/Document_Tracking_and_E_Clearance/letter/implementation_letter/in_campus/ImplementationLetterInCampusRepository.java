package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImplementationLetterInCampusRepository extends JpaRepository<ImplementationLetterInCampus, Integer> {

    @Query("SELECT i FROM ImplementationLetterInCampus i WHERE i.status =:status")
    Page<ImplementationLetterInCampus> findAll(@Param("status") LetterStatus status, Pageable pageable);
}

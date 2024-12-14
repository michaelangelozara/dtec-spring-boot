package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.LetterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunicationLetterRepository extends JpaRepository<CommunicationLetter, Integer> {

    @Query("""
                SELECT c FROM CommunicationLetter c WHERE c.type = :type
            """)
    Page<CommunicationLetter> findAll(@Param("type") CommunicationLetterType type, Pageable pageable);

    @Query("SELECT c FROM CommunicationLetter c WHERE c.status =:status")
    Page<CommunicationLetter> findAll(@Param("status") LetterStatus status, Pageable pageable);
}

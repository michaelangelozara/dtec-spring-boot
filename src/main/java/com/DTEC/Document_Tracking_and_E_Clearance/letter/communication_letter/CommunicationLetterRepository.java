package com.DTEC.Document_Tracking_and_E_Clearance.letter.communication_letter;

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

    @Query("SELECT c FROM CommunicationLetter c JOIN c.signedPeople sp WHERE STR(c.currentLocation) =:currentLocation OR sp.user.id =:userId")
    Page<CommunicationLetter> findAll(@Param("currentLocation") String currentLocation, Pageable pageable, @Param("userId") int userId);

    @Query("SELECT c FROM CommunicationLetter c JOIN c.club cl JOIN cl.memberRoles mr WHERE mr.user.id =:id AND STR(mr.role) =:role")
    Page<CommunicationLetter> findAll(Pageable pageable, @Param("id") int id, @Param("role") String role);
}

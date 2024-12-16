package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImplementationLetterOffCampusRepository extends JpaRepository<ImplementationLetterOffCampus, Integer> {

    @Query("SELECT i FROM ImplementationLetterOffCampus i WHERE STR(i.currentLocation) =:currentLocation")
    Page<ImplementationLetterOffCampus> findAll(@Param("currentLocation") String currentLocation, Pageable pageable);

    @Query("SELECT i FROM ImplementationLetterOffCampus i " +
            "JOIN i.club c JOIN c.memberRoles mr" +
            " WHERE mr.user.id =:id")
    Page<ImplementationLetterOffCampus> findAll(Pageable pageable, @Param("id") int id);
}

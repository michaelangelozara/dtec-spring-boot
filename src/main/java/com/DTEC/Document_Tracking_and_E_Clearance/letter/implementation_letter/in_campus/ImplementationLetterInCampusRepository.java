package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImplementationLetterInCampusRepository extends JpaRepository<ImplementationLetterInCampus, Integer> {

    @Query("SELECT i FROM ImplementationLetterInCampus i WHERE STR(i.currentLocation) =:currentLocation")
    Page<ImplementationLetterInCampus> findAll(@Param("currentLocation") String currentLocation, Pageable pageable);

    @Query("SELECT i FROM ImplementationLetterInCampus i " +
            "JOIN i.club c JOIN c.memberRoles mr " +
            "WHERE mr.user.id =:id")
    Page<ImplementationLetterInCampus> findAll(
            Pageable pageable,
            @Param("id") int id
    );
}

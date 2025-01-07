package com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.off_campus;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.implementation_letter.in_campus.ImplementationLetterInCampus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImplementationLetterOffCampusRepository extends JpaRepository<ImplementationLetterOffCampus, Integer> {

    @Query("SELECT i FROM ImplementationLetterOffCampus i JOIN i.signedPeople sp WHERE STR(i.currentLocation) =:currentLocation OR sp.user.id =:userId")
    Page<ImplementationLetterOffCampus> findAll(@Param("currentLocation") String currentLocation, Pageable pageable, @Param("userId") int userI);

    @Query("SELECT i FROM ImplementationLetterOffCampus i " +
            "JOIN i.club c JOIN c.memberRoles mr" +
            " WHERE mr.user.id =:id AND STR(mr.role) =:role")
    Page<ImplementationLetterOffCampus> findAll(
            Pageable pageable,
            @Param("id") int id,
            @Param("role") String role
    );

    @Query("SELECT i FROM ImplementationLetterOffCampus i LEFT JOIN i.signedPeople sp LEFT JOIN sp.user u WHERE (u.firstName LIKE %:query% OR u.lastname LIKE %:query%) AND u.id =:id")
    List<ImplementationLetterOffCampus> findAllOIC(@Param("query") String query, @Param("id") int id);
}

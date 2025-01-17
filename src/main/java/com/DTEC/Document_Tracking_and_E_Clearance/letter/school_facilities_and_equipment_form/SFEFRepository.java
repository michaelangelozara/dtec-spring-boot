package com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SFEFRepository extends JpaRepository<SFEF, Integer> {

    @Query("SELECT s FROM SFEF s LEFT JOIN s.club c LEFT JOIN c.memberRoles mr WHERE mr.user.id =:userId AND STR(mr.role) =:role")
    Page<SFEF> findAll(Pageable pageable, @Param("userId") int userId, @Param("role") String role);

    @Query("SELECT s FROM SFEF s LEFT JOIN s.signedPeople sp WHERE STR(s.currentLocation) =:currentLocation OR sp.user.id =:userId")
    Page<SFEF> findAll(@Param("currentLocation") String currentLocation, Pageable pageable, @Param("userId") int userId);

    @Query("SELECT s FROM SFEF s LEFT JOIN s.signedPeople sp WHERE (STR(s.currentLocation) = 'PPLO' OR STR(s.currentLocation) = 'PRESIDENT') OR sp.user.id =:userId")
    Page<SFEF> findAll(Pageable pageable, @Param("userId") int userId); // this is for Chapel and Multimedia role Only
}

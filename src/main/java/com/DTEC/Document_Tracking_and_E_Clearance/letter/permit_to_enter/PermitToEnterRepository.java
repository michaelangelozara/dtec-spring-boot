package com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PermitToEnterRepository extends JpaRepository<PermitToEnter, Integer> {

    @Query("SELECT p FROM PermitToEnter p LEFT JOIN p.club c LEFT JOIN c.memberRoles mr WHERE mr.user.id =:userId AND STR(mr.role) =:role")
    Page<PermitToEnter> findAll(Pageable pageable, @Param("userId") int userId, @Param("role") String role);

    @Query("SELECT p FROM PermitToEnter p LEFT JOIN p.signedPeople sp WHERE STR(p.currentLocation) =:currentLocation OR sp.user.id =:userId")
    Page<PermitToEnter> findAll(@Param("currentLocation") String currentLocation, Pageable pageable, @Param("userId") int userId);
}

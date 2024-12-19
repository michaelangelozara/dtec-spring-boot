package com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity;

import com.DTEC.Document_Tracking_and_E_Clearance.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Integer> {

    @Query("SELECT mr FROM MemberRole mr JOIN mr.club c JOIN mr.user u  WHERE c.id =:clubId AND u.role =:role")
    List<MemberRole> findMemberRoleByClubId(@Param("clubId") int clubId, @Param("role") Role role);
}

package com.DTEC.Document_Tracking_and_E_Clearance.clearance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClearanceRepository extends JpaRepository<Clearance, Integer> {

    @Query("SELECT c FROM Clearance c JOIN c.user s WHERE s.id =:id")
    Page<Clearance> findAllByStudentId(Pageable pageable, @Param("id") int id);

    // this is for dean role only
    @Query("""
                SELECT c FROM Clearance c
                            LEFT JOIN c.user s
                            LEFT JOIN s.department d
                            LEFT JOIN d.users u
                            WHERE (u.id =:deanId
                            AND u.role = 'DEAN'
                            AND c.status != 'COMPLETED'
                            AND c.isSubmitted = true 
                            AND (s.role = 'STUDENT' OR s.role = 'STUDENT_OFFICER'))
                            OR 
                            (u.id =:deanId
                            AND u.role = 'DEAN'
                            AND c.status != 'COMPLETED' 
                            AND c.isSubmitted = true
                            AND s.role = 'PERSONNEL' 
                            AND s.type = 'ACADEMIC')
            """)
    List<Clearance> findAllForDean(@Param("deanId") int deanId, @Param("dep_id") int dep_id);

    // this is for program head role only
    @Query("""
                SELECT c FROM Clearance c
                        LEFT JOIN c.user s
                        LEFT JOIN s.course co
                        LEFT JOIN co.users u
                        WHERE (u.id =:programHeadId
                        AND u.role = 'PROGRAM_HEAD'
                        AND c.status != 'COMPLETED'
                        AND c.isSubmitted = true 
                        AND (s.role = 'STUDENT' OR s.role = 'STUDENT_OFFICER')) 
                        OR 
                        (u.id =:programHeadId
                        AND u.role = 'PROGRAM_HEAD'
                        AND c.status != 'COMPLETED'
                        AND c.isSubmitted = true
                        AND s.role = 'PERSONNEL'
                        AND s.type = 'ACADEMIC')
            """)
    List<Clearance> findAllForProgramHead(@Param("programHeadId") int programHeadId);

    @Query("SELECT c FROM Clearance c WHERE c.user.id =:userId ORDER BY c.id DESC")
    List<Clearance> findClearanceByUserId(@Param("userId") int userId);

    @Query("SELECT c FROM Clearance c WHERE c.status != 'COMPLETED' AND c.isSubmitted = true")
    List<Clearance> findAll();

    @Query("SELECT c FROM Clearance c WHERE c.status != 'COMPLETED' AND c.type = 'PERSONNEL_CLEARANCE'")
    List<Clearance> findAllClearancesForVPAFAndVPAAndPresident();
}

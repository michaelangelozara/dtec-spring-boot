package com.DTEC.Document_Tracking_and_E_Clearance.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT s FROM User s WHERE STR(s.role) = 'STUDENT' OR STR(s.role) = 'STUDENT_OFFICER'")
    List<User> findAllStudents();

    @Query("SELECT s FROM User s WHERE STR(s.role) = 'PERSONNEL' OR STR(s.role) = 'MODERATOR'")
    List<User> findAllPersonnel();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Integer countSuperAdmin(@Param("role") Role role);

    @Query("SELECT u FROM User u " +
            "WHERE u.role = 'GUIDANCE' " +
            "OR u.role = 'CASHIER' " +
            "OR u.role = 'LIBRARIAN' " +
            "OR u.role = 'SCHOOL_NURSE' " +
            "OR u.role = 'REGISTRAR' " +
            "OR u.role = 'SCIENCE_LAB' " +
            "OR u.role = 'COMPUTER_SCIENCE_LAB' " +
            "OR u.role = 'ELECTRONICS_LAB' " +
            "OR u.role = 'CRIM_LAB' " +
            "OR u.role = 'HRM_LAB' " +
            "OR u.role = 'NURSING_LAB' " +
            "OR u.role = 'DSA'")
    List<User> findAllOfficeInChargeForStudentClearance();

    @Query("SELECT u FROM User u " +
            "WHERE u.role = 'SCIENCE_LAB' " +
            "OR u.role = 'CRIM_LAB' " +
            "OR u.role = 'COMPUTER_SCIENCE_LAB' " +
            "OR u.role = 'ELECTRONICS_LAB' " +
            "OR u.role = 'HRM_LAB' " +
            "OR u.role = 'NURSING_LAB' " +
            "OR u.role = 'MULTIMEDIA' " +
            "OR u.role = 'LIBRARIAN' " +
            "OR u.role = 'CASHIER' " +
            "OR u.role = 'REGISTRAR' " +
            "OR u.role = 'ACCOUNTING_CLERK' " +
            "OR u.role = 'FINANCE' " +
            "OR u.role = 'CUSTODIAN' " +
            "OR u.role = 'PROGRAM_HEAD' " +
            "OR u.role = 'DEAN' " +
            "OR u.role = 'VPAF' " +
            "OR u.role = 'VPA' " +
            "OR u.role = 'PRESIDENT'")
    List<User> findAllOfficeInChargeForPersonnelClearance();
}

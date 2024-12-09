package com.DTEC.Document_Tracking_and_E_Clearance.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u")
    List<User> findAllWithLimit(Pageable pageable);

    @Query("SELECT s FROM User s WHERE STR(s.role) = 'STUDENT' OR STR(s.role) = 'STUDENT_OFFICER'")
    List<User> findAllStudents();

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Integer countSuperAdmin(@Param("role") Role role);
}

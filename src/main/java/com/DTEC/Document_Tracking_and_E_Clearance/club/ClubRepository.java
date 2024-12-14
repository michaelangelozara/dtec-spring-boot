package com.DTEC.Document_Tracking_and_E_Clearance.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClubRepository extends JpaRepository<Club, Integer> {

    @Query("SELECT COUNT(c) FROM Club c")
    int countRow();
}

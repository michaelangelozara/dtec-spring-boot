package com.DTEC.Document_Tracking_and_E_Clearance.fingerprint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FingerprintRepository extends JpaRepository<Fingerprint, Integer> {

    @Query("SELECT f FROM Fingerprint f WHERE f.user.id =:id")
    List<Fingerprint> findAllByUserId(@Param("id") int id);
}

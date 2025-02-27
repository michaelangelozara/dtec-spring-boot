package com.DTEC.Document_Tracking_and_E_Clearance.e_signature;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ESignatureRepository extends JpaRepository<ESignature, Integer> {

    @Query("SELECT e FROM ESignature e JOIN e.fingerprints f WHERE f.user.username = ?1")
    List<ESignature> findByUsername(String username);
}

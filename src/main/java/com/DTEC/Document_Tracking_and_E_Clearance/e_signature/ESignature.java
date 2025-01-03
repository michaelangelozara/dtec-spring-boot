package com.DTEC.Document_Tracking_and_E_Clearance.e_signature;

import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.Fingerprint;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "e_signatures")
@EntityListeners(AuditingEntityListener.class)
public class ESignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String image;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @OneToMany(mappedBy = "eSignature", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Fingerprint> fingerprints;
}

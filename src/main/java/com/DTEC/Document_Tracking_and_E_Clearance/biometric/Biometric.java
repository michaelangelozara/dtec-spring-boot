package com.DTEC.Document_Tracking_and_E_Clearance.biometric;

import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Entity
@Table(name = "biometrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Biometric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private String data;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}

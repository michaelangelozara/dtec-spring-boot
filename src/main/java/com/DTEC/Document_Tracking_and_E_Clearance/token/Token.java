package com.DTEC.Document_Tracking_and_E_Clearance.token;

import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    @NotBlank(message = "Refresh Token cannot be Blank")
    private String refreshToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    @Column(name = "is_revoked")
    private boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}

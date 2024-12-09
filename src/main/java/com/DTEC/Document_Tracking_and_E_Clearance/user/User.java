package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.biometric.Biometric;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.Clearance;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubAssignedRole;
import com.DTEC.Document_Tracking_and_E_Clearance.club.ClubRole;
import com.DTEC.Document_Tracking_and_E_Clearance.course.Course;
import com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter.ImplementationLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.token.Token;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "First Name cannot be Blank")
    @Size(max = 50, message = "First name must not exceed 50 Characters")
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Size(max = 50, message = "Middle Name must not exceed 50 Characters")
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotBlank(message = "Lastname cannot be Blank")
    @Size(max = 50, message = "Lastname must not exceed 50 Characters")
    @Column(length = 50, nullable = false)
    private String lastname;

    private LocalDate birthDate;

    private String address;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @NotBlank(message = "User Id cannot be Blank")
    @Size(max = 16, message = "User Id must not exceed 16 Characters")
    @Column(length = 16, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password cannot be Blank")
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Max(3)
    @Min(0)
    @Column(name = "registered_fingerprint")
    private int registeredFingerprint;

    @Lob
    private String signature;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<Biometric> biometrics;

    @OneToMany(mappedBy = "student", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Clearance> clearances;

    @OneToMany(mappedBy = "personnel")
    @JsonManagedReference
    private List<ClearanceSignoff> clearanceSignoffs;

    @OneToOne
    @JoinColumn(name = "social_club_id")
    private Club socialClub;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Token> tokens;

    @OneToMany(mappedBy = "mayor")
    @JsonManagedReference
    private List<ImplementationLetter> implementationLettersAsMayor;

    @OneToMany(mappedBy = "moderator")
    @JsonManagedReference
    private List<ImplementationLetter> implementationLettersAsModerator;

    @OneToOne(mappedBy = "user")
    private ClubAssignedRole clubAssignedRole;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return !isDeleted;
    }
}

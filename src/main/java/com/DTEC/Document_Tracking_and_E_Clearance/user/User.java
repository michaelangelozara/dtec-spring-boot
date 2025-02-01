package com.DTEC.Document_Tracking_and_E_Clearance.user;

import com.DTEC.Document_Tracking_and_E_Clearance.clearance.Clearance;
import com.DTEC.Document_Tracking_and_E_Clearance.clearance.clearance_signoff.ClearanceSignoff;
import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRole;
import com.DTEC.Document_Tracking_and_E_Clearance.course.Course;
import com.DTEC.Document_Tracking_and_E_Clearance.department.Department;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.permit_to_enter.PermitToEnter;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.school_facilities_and_equipment_form.SFEF;
import com.DTEC.Document_Tracking_and_E_Clearance.letter.signed_people.SignedPeople;
import com.DTEC.Document_Tracking_and_E_Clearance.token.Token;
import com.DTEC.Document_Tracking_and_E_Clearance.fingerprint.Fingerprint;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
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

    @Column(name = "year_level")
    private Integer yearLevel;

    @Column(name = "is_first_time_login")
    private boolean isFirstTimeLogin;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @Column(name = "contact_number", unique = true)
    private String contactNumber;

    @NotBlank(message = "User Id cannot be Blank")
    @Size(max = 16, message = "User Id must not exceed 16 Characters")
    @Column(length = 16, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password cannot be Blank")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String eSignature;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private PersonnelType type;

    @Enumerated(EnumType.STRING)
    private Office office;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Fingerprint> fingerprints;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    @JsonManagedReference
    private List<Clearance> clearances;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<ClearanceSignoff> clearanceSignoffs;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<SignedPeople> signedPeople;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<PermitToEnter> permitToEnters;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<SFEF> sfefs;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Token> tokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<MemberRole> memberRoles;

    @ManyToOne
    @JsonBackReference
    private Department department;

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

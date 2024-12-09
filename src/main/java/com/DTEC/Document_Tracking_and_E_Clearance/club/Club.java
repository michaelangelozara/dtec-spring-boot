package com.DTEC.Document_Tracking_and_E_Clearance.club;

import com.DTEC.Document_Tracking_and_E_Clearance.course.Course;
import com.DTEC.Document_Tracking_and_E_Clearance.implementation_letter.ImplementationLetter;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
@ToString
@Entity
@Table(name = "clubs")
@EntityListeners(AuditingEntityListener.class)
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255, message = "Club Title must not exceed 255 Characters")
    @Column(nullable = false)
    private String name;

    @Lob
    private String logo;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "last_modified", insertable = false)
    private LocalDate lastModified;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @OneToOne(mappedBy = "socialClub")
    private User user;

    @OneToOne(mappedBy = "departmentClub", cascade = CascadeType.PERSIST)
    private Course course;

    @OneToMany(mappedBy = "club")
    @JsonManagedReference
    private List<ClubAssignedRole> clubAssignedRoles;

    @OneToMany(mappedBy = "club")
    @JsonManagedReference
    private List<ImplementationLetter> implementationLetters;
}

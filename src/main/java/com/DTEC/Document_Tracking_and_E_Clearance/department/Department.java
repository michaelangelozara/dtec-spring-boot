package com.DTEC.Document_Tracking_and_E_Clearance.department;

import com.DTEC.Document_Tracking_and_E_Clearance.club.Club;
import com.DTEC.Document_Tracking_and_E_Clearance.course.Course;
import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "departments")
@EntityListeners(AuditingEntityListener.class)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Department Title cannot be Blank")
    @Size(max = 255, message = "Department Title must not exceed 255 Characters")
    @Column(nullable = false)
    private String name;

    @Lob
    private String logo;

    @NotBlank(message = "Department Code cannot be Blank")
    @Size(max = 50, message = "Department Code must not exceed 50 Characters")
    @Column(name = "department_code", length = 50)
    private String departmentCode;

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

    @OneToMany(mappedBy = "department")
    @JsonManagedReference
    private List<Course> courses;
}

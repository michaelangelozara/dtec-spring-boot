package com.DTEC.Document_Tracking_and_E_Clearance.department;

import com.DTEC.Document_Tracking_and_E_Clearance.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
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

    @NotBlank(message = "Department Code cannot be Blank")
    @Size(max = 50, message = "Department Code must not exceed 50 Characters")
    @Column(name = "department_code", length = 50)
    private String departmentCode;

    @Column(name = "short_name", length = 15, nullable = false)
    private String shortName;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @OneToMany(mappedBy = "department")
    @JsonManagedReference
    private List<User> users;
}

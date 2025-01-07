package com.DTEC.Document_Tracking_and_E_Clearance.club;

import com.DTEC.Document_Tracking_and_E_Clearance.letter.budget_proposal.BudgetProposal;
import com.DTEC.Document_Tracking_and_E_Clearance.club.sub_entity.MemberRole;
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

    @Column(name = "short_name", nullable = false, length = 15)
    private String shortName;

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

    @Enumerated(EnumType.STRING)
    private Type type;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String logo;

    @OneToMany(mappedBy = "club", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<MemberRole> memberRoles;

    @OneToMany(mappedBy = "club")
    @JsonManagedReference
    private List<BudgetProposal> budgetProposals;
}

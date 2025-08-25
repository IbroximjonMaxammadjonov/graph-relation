package com.ibroximjon.graphrelation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asoschi bo'lgan kompaniya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_company_id", nullable = false)
    private Company parentCompany;

    // Asos solingan kompaniya
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_company_id", nullable = false)
    private Company childCompany;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationType relationType;

    public enum RelationType {
        FOUNDER
    }
}


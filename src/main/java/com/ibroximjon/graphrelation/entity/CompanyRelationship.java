package com.ibroximjon.graphrelation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_company_inn", referencedColumnName = "inn", nullable = false)
    private Company parentCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_company_inn", referencedColumnName = "inn", nullable = false)
    private Company childCompany;

    @Column(precision = 5, scale = 2)
    private BigDecimal founderShare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationType relationType;

    public enum RelationType { FOUNDER }
}

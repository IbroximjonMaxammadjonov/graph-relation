package com.ibroximjon.graphrelation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;



@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_inn", referencedColumnName = "inn", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_pinfl", referencedColumnName = "pinfl", nullable = false)
    private Person person;

    @Column(precision = 5, scale = 2)
    private BigDecimal founderShare;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role { DIRECTOR, FOUNDER }
}

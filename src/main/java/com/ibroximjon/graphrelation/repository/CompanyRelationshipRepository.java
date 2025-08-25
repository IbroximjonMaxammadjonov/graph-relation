package com.ibroximjon.graphrelation.repository;

import com.ibroximjon.graphrelation.entity.CompanyRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRelationshipRepository extends JpaRepository<CompanyRelationship, Long> {

    // Bitta company ning child (asos solingan) kompaniyalarini olish
    List<CompanyRelationship> findByParentCompanyId(Long parentCompanyId);

    // Bitta company kim tomonidan asos solinganini olish
    List<CompanyRelationship> findByChildCompanyId(Long childCompanyId);

    // Duplicate tekshirish uchun
    boolean existsByParentCompanyIdAndChildCompanyId(Long parentId, Long childId);
}

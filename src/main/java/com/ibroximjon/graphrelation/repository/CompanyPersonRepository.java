package com.ibroximjon.graphrelation.repository;
import com.ibroximjon.graphrelation.entity.Company;
import com.ibroximjon.graphrelation.entity.CompanyPerson;
import com.ibroximjon.graphrelation.entity.Person;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CompanyPersonRepository extends JpaRepository<CompanyPerson, Long> {
    // kompaniya bo‘yicha aloqadorlarni topish
    @EntityGraph(attributePaths = {"person"})
    List<CompanyPerson> findByCompany(Company company);

    // shaxs bo‘yicha qaysi kompaniyalarga aloqadorligini topish

    @EntityGraph(attributePaths = {"company"})
    List<CompanyPerson> findByPerson(Person person);


}


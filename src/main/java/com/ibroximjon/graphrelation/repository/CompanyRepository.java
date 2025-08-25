package com.ibroximjon.graphrelation.repository;
import com.ibroximjon.graphrelation.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByInn(String inn);
}


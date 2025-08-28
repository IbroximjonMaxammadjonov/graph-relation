package com.ibroximjon.graphrelation.repository;

import com.ibroximjon.graphrelation.entity.Person;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Cacheable("personByPinfl")
    Optional<Person> findByPinfl(String pinfl);
}

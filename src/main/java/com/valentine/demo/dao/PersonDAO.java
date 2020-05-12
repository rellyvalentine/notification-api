package com.valentine.demo.dao;

import com.valentine.demo.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonDAO extends JpaRepository<Person, Long> {

    @Override
    List<Person> findAll();

    @Query(nativeQuery = true, value = "SELECT person.id, person.first_name, person.last_name, person.age, person.gender, person.race FROM person " +
            "WHERE id = ?1 ")
    Person findById(@Param("id") long id);
}

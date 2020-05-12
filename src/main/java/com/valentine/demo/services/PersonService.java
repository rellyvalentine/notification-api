package com.valentine.demo.services;

import com.valentine.demo.dao.PersonDAO;
import com.valentine.demo.entities.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    PersonDAO personRepo;

    public Person getPersonById(long id){
        return personRepo.findById(id);
    }

    public List<Person> getAllPeople(){
        return personRepo.findAll();
    }

    public void savePerson(Person person){
        personRepo.save(person);
    }

}

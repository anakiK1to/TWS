package ru.itmo.standalone_server.service;

import ru.itmo.standalone_server.model.Person;
import ru.itmo.standalone_server.repository.PersonRepository;

import java.util.List;

public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> searchPersons(String query, int limit, int offset) {
        return personRepository.findPerson(query, limit, offset);
    }
}


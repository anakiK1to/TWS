package ru.itmo.j2ee.service;



import ru.itmo.j2ee.model.entity.Person;
import ru.itmo.j2ee.repository.PersonRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PersonService {

    @Inject
    PersonRepository personRepository;

    public PersonService() {

    }
    public List<Person> searchPersons(String query, int limit, int offset) {
        return personRepository.findPerson(query, limit, offset);
    }
}

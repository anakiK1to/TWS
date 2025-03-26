package ru.itmo.standalone_server.service;

import ru.itmo.standalone_server.mapper.PersonMapper;
import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;
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


    public Person getPerson(int id) {
        return personRepository.getById(id);
    }

    public int createPerson(PersonDto personDto) {
        Person person = PersonMapper.toEntity(personDto);
        return personRepository.create(person);
    }

    public boolean updatePerson(int id, PersonDto personDto) {
        Person person = PersonMapper.toEntity(personDto);
        return personRepository.updateById(id, person);
    }

    public boolean deletePerson(int id) {
        return personRepository.deleteById(id);
    }
}


package ru.itmo.standalone_server.server;

import ru.itmo.standalone_server.exceptions.WebException;
import ru.itmo.standalone_server.model.dtos.GetPersonsRequestDto;
import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;
import ru.itmo.standalone_server.service.PersonService;
import ru.itmo.standalone_server.service.PersonValidation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.Collections;
import java.util.List;

@javax.jws.WebService(    serviceName = "PersonService",
        targetNamespace = "http://ru.itmo.standalone_server/", // Ваш кастомный namespace
        portName = "PersonServicePort")
public class WebService {
    private final PersonService personService;

    public WebService(PersonService personService) {
        this.personService = personService;
    }

    @WebMethod
    public List<Person> getPersons(@WebParam(name = "arg0") GetPersonsRequestDto personListRequestDto) {
        if (personListRequestDto == null) {
            System.out.println("Just received incorrect GetPersonsRequestDto");
            return Collections.emptyList();
        }

        int limit = personListRequestDto.getLimit() != null ? personListRequestDto.getLimit() : 20;
        int offset = personListRequestDto.getOffset() != null ? personListRequestDto.getOffset() : 0;

        return personService.searchPersons(personListRequestDto.getQuery(), limit, offset);
    }

    @WebMethod
    public Person findPersonById(@WebParam(name = "id") int id) {
        return personService.getPerson(id);
    }

    @WebMethod
    public int createPerson(@WebParam(name = "personDto") PersonDto personDto) throws WebException {
        PersonValidation.validatePersonDto(personDto);

        return personService.createPerson(personDto);
    }

    @WebMethod
    public boolean updatePerson(@WebParam(name = "id") int id, @WebParam(name = "personDto") PersonDto personDto) throws WebException {
        PersonValidation.validatePersonDto(personDto);
        return personService.updatePerson(id, personDto);
    }

    @WebMethod
    public boolean deletePersonById(@WebParam(name = "id") int id) {
        return personService.deletePerson(id);
    }
}

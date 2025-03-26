package ru.itmo.j2ee.server;


import ru.itmo.j2ee.model.dto.GetPersonsRequestDto;
import ru.itmo.j2ee.model.entity.Person;
import ru.itmo.j2ee.service.PersonService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import java.util.Collections;
import java.util.List;

@ApplicationScoped
@javax.jws.WebService(serviceName = "PersonService")
public class WebService {
    @Inject
    PersonService personService;

    public WebService() {
    }

    @WebMethod
    public List<Person> getPersons(@WebParam(name = "arg0") GetPersonsRequestDto personListRequestDto) {
        if (personListRequestDto == null) {
            System.out.println("Received null PersonListRequestDto");
            return Collections.emptyList();
        }

        int limit = personListRequestDto.getLimit() != null ? personListRequestDto.getLimit() : 20;
        int offset = personListRequestDto.getOffset() != null ? personListRequestDto.getOffset() : 0;

        return personService.searchPersons(personListRequestDto.getQuery(), limit, offset);
    }
}

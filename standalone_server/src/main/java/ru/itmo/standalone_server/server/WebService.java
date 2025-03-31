package ru.itmo.standalone_server.server;

import ru.itmo.standalone_server.exceptions.WebException;
import ru.itmo.standalone_server.model.dtos.GetPersonsRequestDto;
import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;
import ru.itmo.standalone_server.service.PersonService;
import ru.itmo.standalone_server.service.PersonValidation;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@javax.jws.WebService(    serviceName = "PersonService",
        targetNamespace = "http://ru.itmo.standalone_server/",
        portName = "PersonServicePort")
public class WebService {
    private final PersonService personService;


    @Resource
    private WebServiceContext context;

    public WebService(PersonService personService) {
        this.personService = personService;
    }

    private static final String AUTH_USERNAME = "admin";
    private static final String AUTH_PASSWORD = "password";

    private boolean isAuthenticated() {
        try {
            MessageContext messageContext = context.getMessageContext();
            Map<String, List<String>> headers = (Map<String, List<String>>) messageContext.get(MessageContext.HTTP_REQUEST_HEADERS);

            List<String> authHeaders = headers.get("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                return false;
            }

            String authHeader = authHeaders.get(0);
            if (!authHeader.startsWith("Basic ")) {
                return false;
            }

            String encodedCredentials = authHeader.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(encodedCredentials));
            String[] parts = credentials.split(":");

            if (parts.length != 2) {
                return false;
            }

            String username = parts[0];
            String password = parts[1];

            return AUTH_USERNAME.equals(username) && AUTH_PASSWORD.equals(password);
        } catch (Exception e) {
            return false;
        }
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

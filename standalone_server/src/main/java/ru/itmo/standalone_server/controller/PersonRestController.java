package ru.itmo.standalone_server.controller;

import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;
import ru.itmo.standalone_server.service.PersonService;
import ru.itmo.standalone_server.service.PersonValidation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonRestController {
    private final PersonService personService;


    private static final String AUTH_USERNAME = "admin";
    private static final String AUTH_PASSWORD = "password";

    @Inject
    public PersonRestController(PersonService personService) {
        this.personService = personService;
    }

    private boolean isAuthenticated(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }

        String encodedCredentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(encodedCredentials));
        StringTokenizer tokenizer = new StringTokenizer(credentials, ":");

        if (tokenizer.countTokens() != 2) {
            return false;
        }

        String username = tokenizer.nextToken();
        String password = tokenizer.nextToken();

        return AUTH_USERNAME.equals(username) && AUTH_PASSWORD.equals(password);
    }

    @GET
    public Response searchPersons(@QueryParam("query") String query,
                                  @QueryParam("limit") @DefaultValue("20") int limit,
                                  @QueryParam("offset") @DefaultValue("0") int offset) {
        List<Person> persons = personService.searchPersons(query, limit, offset);
        return Response.ok(persons).build();
    }

    @GET
    @Path("/{id}")
    public Response findPersonById(@PathParam("id") int id) {
        Person person = personService.getPerson(id);
        return person != null ? Response.ok(person).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createPerson(@HeaderParam("Authorization") String authHeader, PersonDto personDto) {
        if (!isAuthenticated(authHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                    .build();
        }

        PersonValidation.validatePersonDto(personDto);
        int id = personService.createPerson(personDto);
        return Response.status(Response.Status.CREATED).entity(id).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePerson(@HeaderParam("Authorization") String authHeader,
                                 @PathParam("id") int id, PersonDto personDto) {
        if (!isAuthenticated(authHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                    .build();
        }

        PersonValidation.validatePersonDto(personDto);
        boolean updated = personService.updatePerson(id, personDto);
        return updated ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePersonById(@HeaderParam("Authorization") String authHeader,
                                     @PathParam("id") int id) {
        if (!isAuthenticated(authHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                    .build();
        }

        boolean deleted = personService.deletePerson(id);
        return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
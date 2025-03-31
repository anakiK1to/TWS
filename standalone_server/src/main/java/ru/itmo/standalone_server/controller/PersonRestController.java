package ru.itmo.standalone_server.controller;

import ru.itmo.standalone_server.exceptions.ThrottlingException;
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
import java.util.concurrent.Semaphore;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonRestController {
    private final PersonService personService;

    // Настройки аутентификации
    private static final String AUTH_USERNAME = "admin";
    private static final String AUTH_PASSWORD = "password";

    // Настройки throttling
    private static final int MAX_CONCURRENT_REQUESTS = 10;
    private final Semaphore requestSemaphore = new Semaphore(MAX_CONCURRENT_REQUESTS, true);

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

    private void checkThrottling() throws ThrottlingException {
        if (!requestSemaphore.tryAcquire()) {
            throw new ThrottlingException(
                    "Server busy. Maximum concurrent requests (" + MAX_CONCURRENT_REQUESTS + ") reached. Please try again later.");
        }
    }

    @GET
    public Response searchPersons(@QueryParam("query") String query,
                                  @QueryParam("limit") @DefaultValue("20") int limit,
                                  @QueryParam("offset") @DefaultValue("0") int offset) {
        try {
            checkThrottling();
            List<Person> persons = personService.searchPersons(query, limit, offset);
            return Response.ok(persons).build();
        } catch (ThrottlingException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(e.getMessage())
                    .build();
        } finally {
            requestSemaphore.release();
        }
    }

    @GET
    @Path("/{id}")
    public Response findPersonById(@PathParam("id") int id) {
        try {
            checkThrottling();
            Person person = personService.getPerson(id);
            return person != null ? Response.ok(person).build() : Response.status(Response.Status.NOT_FOUND).build();
        } catch (ThrottlingException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(e.getMessage())
                    .build();
        } finally {
            requestSemaphore.release();
        }
    }

    @POST
    public Response createPerson(@HeaderParam("Authorization") String authHeader,
                                 PersonDto personDto) {

        if (!isAuthenticated(authHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                    .build();
        }

        try {
            checkThrottling();
            PersonValidation.validatePersonDto(personDto);
            int id = personService.createPerson(personDto);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (ThrottlingException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(e.getMessage())
                    .build();
        } finally {
            requestSemaphore.release();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updatePerson(@HeaderParam("Authorization") String authHeader,
                                 @PathParam("id") int id,
                                 PersonDto personDto) {
        if (!isAuthenticated(authHeader)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                    .build();
        }

        try {
            checkThrottling();
            PersonValidation.validatePersonDto(personDto);
            boolean updated = personService.updatePerson(id, personDto);
            return updated ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
        } catch (ThrottlingException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(e.getMessage())
                    .build();
        } finally {
            requestSemaphore.release();
        }
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

        try {
            checkThrottling();
            boolean deleted = personService.deletePerson(id);
            return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
        } catch (ThrottlingException e) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity(e.getMessage())
                    .build();
        } finally {
            requestSemaphore.release();
        }
    }
}
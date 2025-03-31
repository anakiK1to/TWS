package ru.itmo.standalone_server.controller;

import ru.itmo.standalone_server.exceptions.ThrottlingException;
import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;
import ru.itmo.standalone_server.service.PersonService;
import ru.itmo.standalone_server.service.PersonValidation;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.*;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class PersonRestController {
    private final PersonService personService;
    private final ExecutorService executorService;


    private static final String AUTH_USERNAME = "admin";
    private static final String AUTH_PASSWORD = "password";

    private static final int MAX_CONCURRENT_REQUESTS = 10;
    private final Semaphore requestSemaphore = new Semaphore(MAX_CONCURRENT_REQUESTS, true);

    @Inject
    public PersonRestController(PersonService personService) {
        this.personService = personService;
        this.executorService = Executors.newFixedThreadPool(20);
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
    public void searchPersons(@QueryParam("query") String query,
                              @QueryParam("limit") @DefaultValue("20") int limit,
                              @QueryParam("offset") @DefaultValue("0") int offset,
                              @Suspended final AsyncResponse asyncResponse) {

        executorService.submit(() -> {
            try {
                checkThrottling();
                List<Person> persons = personService.searchPersons(query, limit, offset);
                asyncResponse.resume(Response.ok(persons).build());
            } catch (ThrottlingException e) {
                asyncResponse.resume(Response.status(Response.Status.TOO_MANY_REQUESTS)
                        .entity(e.getMessage())
                        .build());
            } catch (Exception e) {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(e.getMessage())
                        .build());
            } finally {
                requestSemaphore.release();
            }
        });
    }

    @GET
    @Path("/{id}")
    public void findPersonById(@PathParam("id") int id,
                               @Suspended final AsyncResponse asyncResponse) {

        executorService.submit(() -> {
            try {
                checkThrottling();
                Person person = personService.getPerson(id);
                if (person != null) {
                    asyncResponse.resume(Response.ok(person).build());
                } else {
                    asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                }
            } catch (ThrottlingException e) {
                asyncResponse.resume(Response.status(Response.Status.TOO_MANY_REQUESTS)
                        .entity(e.getMessage())
                        .build());
            } catch (Exception e) {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(e.getMessage())
                        .build());
            } finally {
                requestSemaphore.release();
            }
        });
    }

    @POST
    public void createPerson(@HeaderParam("Authorization") String authHeader,
                             PersonDto personDto,
                             @Suspended final AsyncResponse asyncResponse) {

        executorService.submit(() -> {
            try {
                if (!isAuthenticated(authHeader)) {
                    asyncResponse.resume(Response.status(Response.Status.UNAUTHORIZED)
                            .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                            .build());
                    return;
                }

                checkThrottling();
                PersonValidation.validatePersonDto(personDto);
                int id = personService.createPerson(personDto);
                asyncResponse.resume(Response.status(Response.Status.CREATED).entity(id).build());
            } catch (ThrottlingException e) {
                asyncResponse.resume(Response.status(Response.Status.TOO_MANY_REQUESTS)
                        .entity(e.getMessage())
                        .build());
            } catch (Exception e) {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(e.getMessage())
                        .build());
            } finally {
                requestSemaphore.release();
            }
        });
    }

    @PUT
    @Path("/{id}")
    public void updatePerson(@HeaderParam("Authorization") String authHeader,
                             @PathParam("id") int id,
                             PersonDto personDto,
                             @Suspended final AsyncResponse asyncResponse) {

        executorService.submit(() -> {
            try {
                if (!isAuthenticated(authHeader)) {
                    asyncResponse.resume(Response.status(Response.Status.UNAUTHORIZED)
                            .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                            .build());
                    return;
                }

                checkThrottling();
                PersonValidation.validatePersonDto(personDto);
                boolean updated = personService.updatePerson(id, personDto);
                if (updated) {
                    asyncResponse.resume(Response.ok().build());
                } else {
                    asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                }
            } catch (ThrottlingException e) {
                asyncResponse.resume(Response.status(Response.Status.TOO_MANY_REQUESTS)
                        .entity(e.getMessage())
                        .build());
            } catch (Exception e) {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(e.getMessage())
                        .build());
            } finally {
                requestSemaphore.release();
            }
        });
    }

    @DELETE
    @Path("/{id}")
    public void deletePersonById(@HeaderParam("Authorization") String authHeader,
                                 @PathParam("id") int id,
                                 @Suspended final AsyncResponse asyncResponse) {

        executorService.submit(() -> {
            try {
                if (!isAuthenticated(authHeader)) {
                    asyncResponse.resume(Response.status(Response.Status.UNAUTHORIZED)
                            .header("WWW-Authenticate", "Basic realm=\"Person Realm\"")
                            .build());
                    return;
                }

                checkThrottling();
                boolean deleted = personService.deletePerson(id);
                if (deleted) {
                    asyncResponse.resume(Response.ok().build());
                } else {
                    asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
                }
            } catch (ThrottlingException e) {
                asyncResponse.resume(Response.status(Response.Status.TOO_MANY_REQUESTS)
                        .entity(e.getMessage())
                        .build());
            } catch (Exception e) {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(e.getMessage())
                        .build());
            } finally {
                requestSemaphore.release();
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        executorService.shutdownNow();
    }
}
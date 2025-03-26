package ru.itmo.standalone_server.controller;


import ru.itmo.standalone_server.model.dtos.PersonDto;
import ru.itmo.standalone_server.model.entity.Person;
import ru.itmo.standalone_server.service.PersonService;
import ru.itmo.standalone_server.service.PersonValidation;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/p1ersons")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonRestController {
    private final PersonService personService;

    @Inject
    public PersonRestController(PersonService personService) {
        this.personService = personService;
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
    public Response createPerson(PersonDto personDto) {
        PersonValidation.validatePersonDto(personDto);
        int id = personService.createPerson(personDto);
        return Response.status(Response.Status.CREATED).entity(id).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePerson(@PathParam("id") int id, PersonDto personDto) {
        PersonValidation.validatePersonDto(personDto);
        boolean updated = personService.updatePerson(id, personDto);
        return updated ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePersonById(@PathParam("id") int id) {
        boolean deleted = personService.deletePerson(id);
        return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}


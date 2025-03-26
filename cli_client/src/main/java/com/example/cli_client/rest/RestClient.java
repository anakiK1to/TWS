package com.example.cli_client.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestClient {
    private static final Logger LOGGER = Logger.getLogger(RestClient.class.getName());
    private final String baseUrl;
    private final Client httpClient;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public RestClient(String baseUrl, Client client) {
        this.baseUrl = baseUrl;
        this.httpClient = client;
//        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public List<PersonDto> searchPersons(String query, int limit, int offset) {
        Response response = null;
        try {
            URI uri = UriBuilder.fromUri(baseUrl)
                    .path("persons")
                    .queryParam("query", query.replace("%", "%25"))
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .build();

            response = httpClient.target(uri)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<PersonDto>>() {});
            } else {
                throw new RuntimeException("Error fetching persons: " + response.readEntity(String.class));
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


    public PersonDto getPersonById(int id) {
        try {
            URI uri = UriBuilder.fromUri(baseUrl)
                    .path("persons/{id}")
                    .build(id);
            Response response = httpClient.target(uri)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            try {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    return response.readEntity(new GenericType<PersonDto>() {});
                } else {
                    String errMsg = response.readEntity(String.class);
                    throw new RuntimeException("Ошибка при получении person: " + errMsg);
                }
            } finally {
                response.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "getPersonById", ex);
            throw new RuntimeException("Exception in getPersonById", ex);
        }
    }

    public int addPerson(PersonDto person) {
        try {
            URI uri = UriBuilder.fromUri(baseUrl)
                    .path("persons")
                    .build();
            Response response = httpClient.target(uri)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(person, MediaType.APPLICATION_JSON));
            try {
                if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                    return response.readEntity(Integer.class);
                } else {
                    String errMsg = response.readEntity(String.class);
                    throw new RuntimeException("Ошибка при создании человека: " + errMsg);
                }
            } finally {
                response.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "addPerson", ex);
            throw new RuntimeException("Exception in addPerson", ex);
        }
    }

    public boolean updatePerson(int id, PersonDto person) {
        try {
            URI uri = UriBuilder.fromUri(baseUrl)
                    .path("persons/{id}")
                    .build(id);
            Response response = httpClient.target(uri)
                    .request(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(person, MediaType.APPLICATION_JSON));
            try {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    return true;
                } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                    return false;
                } else {
                    String errMsg = response.readEntity(String.class);
                    throw new RuntimeException("Ошибка при обновлении человека: " + errMsg);
                }
            } finally {
                response.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "updatePerson", ex);
            throw new RuntimeException("Exception in updatePerson", ex);
        }
    }

    public boolean removePerson(int id) {
        try {
            URI uri = UriBuilder.fromUri(baseUrl)
                    .path("persons/{id}")
                    .build(id);
            Response response = httpClient.target(uri)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();
            try {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    return true;
                } else if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                    return false;
                } else {
                    String errMsg = response.readEntity(String.class);
                    throw new RuntimeException("Ошибка при удалении человека: " + errMsg);
                }
            } finally {
                response.close();
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "removePerson", ex);
            throw new RuntimeException("Exception in removePerson", ex);
        }
    }
}

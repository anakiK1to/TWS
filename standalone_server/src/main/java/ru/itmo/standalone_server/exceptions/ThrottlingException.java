package ru.itmo.standalone_server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ThrottlingException extends WebApplicationException {
    public ThrottlingException(String message) {
        super(Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(message)
                .build());
    }
}
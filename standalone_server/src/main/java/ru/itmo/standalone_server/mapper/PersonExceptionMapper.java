package ru.itmo.standalone_server.mapper;


import ru.itmo.standalone_server.exceptions.WebException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PersonExceptionMapper implements ExceptionMapper<WebException> {

    @Override
    public Response toResponse(WebException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getFaultInfo())
                .build();
    }
}

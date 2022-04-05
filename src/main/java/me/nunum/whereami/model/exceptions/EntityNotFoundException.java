package me.nunum.whereami.model.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntityNotFoundException extends WebApplicationException
        implements ExceptionMapper<EntityNotFoundException> {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String s) {
        super(s, Response.Status.NOT_FOUND);
    }

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

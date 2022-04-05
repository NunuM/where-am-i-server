package me.nunum.whereami.model.exceptions;

import me.nunum.whereami.model.dto.ErrorDTO;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenSubResourceException
        extends WebApplicationException
        implements ExceptionMapper<ForbiddenSubResourceException> {

    public ForbiddenSubResourceException() {
    }

    public ForbiddenSubResourceException(String message) {
        super(message, Response.Status.FORBIDDEN);
    }

    @Override
    public Response toResponse(ForbiddenSubResourceException exception) {
        return Response
                .status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(ErrorDTO.fromError(exception))
                .build();
    }
}

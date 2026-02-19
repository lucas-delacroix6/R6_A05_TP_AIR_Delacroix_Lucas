package com.example.tp_air.api.mapper;

import com.example.tp_air.dto.ErrorResponse;
import com.example.tp_air.exceptions.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    @Override
    public Response toResponse(ForbiddenException ex) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse("FORBIDDEN", ex.getMessage()))
                .build();
    }
}
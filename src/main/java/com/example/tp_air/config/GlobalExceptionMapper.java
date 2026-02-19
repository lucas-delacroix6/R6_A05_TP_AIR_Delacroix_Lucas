package com.example.tp_air.config;

import com.example.tp_air.dto.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        String message = exception.getMessage();
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String errorType = "SERVER ERROR";

        if (message != null) {
            if (message.contains("404")) {
                status = Response.Status.NOT_FOUND;
                errorType = "NOT FOUND";
            } else if (message.contains("403")) {
                status = Response.Status.FORBIDDEN;
                errorType = "ACCESS DENIED";
            } else if (message.contains("409") || message.contains("Conflict")) {
                status = Response.Status.CONFLICT;
                errorType = "BUSINESS CONFLICT";
            } else if (message.contains("VALIDATION_ERROR")) {
                status = Response.Status.BAD_REQUEST;
                errorType = "VALIDATION ERROR";
            }
        }

        ErrorResponse errorBody = new ErrorResponse(
            errorType,
            Collections.singletonList(message != null ? message : "An unexpected error occurred")
        );

        return Response.status(status)
            .type(MediaType.APPLICATION_JSON)
            .entity(errorBody)
            .build();
    }
}
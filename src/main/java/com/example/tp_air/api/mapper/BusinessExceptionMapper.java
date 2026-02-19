package com.example.tp_air.api.mapper;

import com.example.tp_air.dto.ErrorResponse;
import com.example.tp_air.exceptions.BusinessException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {
    @Override
    public Response toResponse(BusinessException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse("BUSINESS_CONFLICT", ex.getMessage()))
                .build();
    }
}
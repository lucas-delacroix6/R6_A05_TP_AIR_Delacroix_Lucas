package com.example.tp_air.api;

import com.example.tp_air.api.mapper.*;
import com.example.tp_air.resources.AnnonceResource;
import com.example.tp_air.security.filter.AuthFilter;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class MasterAnnonceApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        classes.add(HelloWorldResource.class);
        classes.add(AnnonceResource.class);
        classes.add(AuthResource.class);

        // Filters & Exception Mappers
        classes.add(AuthFilter.class);
        classes.add(NotFoundExceptionMapper.class);
        classes.add(BusinessExceptionMapper.class);
        classes.add(ForbiddenExceptionMapper.class);
        classes.add(ValidationExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);

        // OpenAPI
        classes.add(OpenApiResource.class);

        return classes;
    }
}
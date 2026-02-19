package com.example.tp_air.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class TestResource {

    @GET
    @Path("/helloWorld")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() {
        return Response.ok("{\"message\": \"Hello World\"}").build();
    }

    @GET
    @Path("/params/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testParams(
            @PathParam("id") Long id,
            @QueryParam("name") String name) {
        return Response.ok("{\"id\": " + id + ", \"name\": \"" + name + "\"}").build();
    }
}
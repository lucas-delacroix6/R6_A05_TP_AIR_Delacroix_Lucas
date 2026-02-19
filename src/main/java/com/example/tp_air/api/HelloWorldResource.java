package com.example.tp_air.api;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

/**
 * Exercice 1 – Simple test endpoints demonstrating JAX-RS setup.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldResource {

    /**
     * GET /api/helloWorld
     * Simple hello endpoint to validate JAX-RS is working.
     */
    @GET
    @Path("/helloWorld")
    public Response helloWorld() {
        return Response.ok(Map.of("message", "Hello, World! MasterAnnonce API is running.")).build();
    }

    /**
     * GET /api/params?name=foo
     * Demonstrates QueryParam usage.
     */
    @GET
    @Path("/params")
    public Response withQueryParam(@QueryParam("name") @DefaultValue("World") String name) {
        return Response.ok(Map.of(
                "type", "QueryParam",
                "value", name,
                "message", "Hello, " + name + "!"
        )).build();
    }

    /**
     * GET /api/params/{value}
     * Demonstrates PathParam usage.
     */
    @GET
    @Path("/params/{value}")
    public Response withPathParam(@PathParam("value") String value) {
        return Response.ok(Map.of(
                "type", "PathParam",
                "value", value,
                "message", "Received path param: " + value
        )).build();
    }
}
package com.example.tp_air.resources;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.security.filter.Secured;
import com.example.tp_air.services.AnnonceService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private AnnonceService annonceService;

    @Context
    private ContainerRequestContext requestContext;

    public AnnonceResource() {
        this.annonceService = new AnnonceService();
    }

    @Inject
    public void setAnnonceService(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    @GET
    public Response getAllAnnonces(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return Response.ok(annonceService.findAll(page, size)).build();
    }

    @GET
    @Path("/{id}")
    public Response getAnnonce(@PathParam("id") Long id) {
        return Response.ok(annonceService.findById(id)).build();
    }

    @POST
    @Secured
    public Response createAnnonce(@Valid AnnonceDTO dto, @Context UriInfo uriInfo) {
        Long userId = getCurrentUserId();
        AnnonceDTO created = annonceService.create(dto, userId);
        return Response.created(
                uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build()
        ).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Secured
    public Response updateAnnonce(@PathParam("id") Long id, @Valid AnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO updated = annonceService.update(id, dto, userId);
        return Response.ok(updated).build();
    }

    @PATCH
    @Path("/{id}")
    @Secured
    public Response patchAnnonce(@PathParam("id") Long id, @Valid PatchAnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO updated = annonceService.patch(id, dto, userId);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteAnnonce(@PathParam("id") Long id) {
        Long userId = getCurrentUserId();
        annonceService.delete(id, userId);
        return Response.noContent().build();
    }

    private Long getCurrentUserId() {
        return (Long) requestContext.getProperty("userId");
    }
}
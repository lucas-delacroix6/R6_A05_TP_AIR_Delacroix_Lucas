package com.example.tp_air.api;

import com.example.tp_air.dto.AnnonceDTO;
import com.example.tp_air.dto.PagedResponse;
import com.example.tp_air.dto.PatchAnnonceDTO;
import com.example.tp_air.security.filter.Secured;
import com.example.tp_air.services.AnnonceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Annonces", description = "CRUD operations on Annonces")
public class AnnonceResource {

    private final AnnonceService annonceService = new AnnonceService();

    @Context
    private ContainerRequestContext requestContext;

    // GET /api/annonces?page=0&size=10
    @GET
    @Operation(summary = "List annonces (paginated)")
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        if (size > 100) size = 100;
        PagedResponse<AnnonceDTO> result = annonceService.findAll(page, size);
        return Response.ok(result).build();
    }

    // GET /api/annonces/{id}
    @GET
    @Path("/{id}")
    @Operation(summary = "Get annonce by ID")
    public Response getById(@PathParam("id") Long id) {
        AnnonceDTO dto = annonceService.findById(id);
        return Response.ok(dto).build();
    }

    // POST /api/annonces (protected)
    @POST
    @Secured
    @Operation(summary = "Create annonce", security = @SecurityRequirement(name = "BearerAuth"))
    public Response create(@Valid AnnonceDTO dto, @Context UriInfo uriInfo) {
        Long userId = getCurrentUserId();
        AnnonceDTO created = annonceService.create(dto, userId);
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(created.getId())).build();
        return Response.created(location).entity(created).build();
    }

    // PUT /api/annonces/{id} (protected)
    @PUT
    @Path("/{id}")
    @Secured
    @Operation(summary = "Update annonce (full)", security = @SecurityRequirement(name = "BearerAuth"))
    public Response update(@PathParam("id") Long id, @Valid AnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO updated = annonceService.update(id, dto, userId);
        return Response.ok(updated).build();
    }

    // PATCH /api/annonces/{id} (protected)
    // Partial update: only provided fields are applied
    @PATCH
    @Path("/{id}")
    @Secured
    @Operation(summary = "Partially update annonce", security = @SecurityRequirement(name = "BearerAuth"))
    public Response patch(@PathParam("id") Long id, @Valid PatchAnnonceDTO dto) {
        Long userId = getCurrentUserId();
        AnnonceDTO updated = annonceService.patch(id, dto, userId);
        return Response.ok(updated).build();
    }

    // DELETE /api/annonces/{id} (protected)
    @DELETE
    @Path("/{id}")
    @Secured
    @Operation(summary = "Delete annonce (must be ARCHIVED first)", security = @SecurityRequirement(name = "BearerAuth"))
    public Response delete(@PathParam("id") Long id) {
        Long userId = getCurrentUserId();
        annonceService.delete(id, userId);
        return Response.noContent().build();
    }

    private Long getCurrentUserId() {
        return (Long) requestContext.getProperty("userId");
    }
}
package com.communityPantry.communityPantry.web.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.communityPantry.communityPantry.domain.Tag;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.communityPantry.communityPantry.exception.ApiError;

// the Tag entity clashes with the @Tag annotation from swagger
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Endpoints for managing tags")
public interface TagControllerInterface {

    @Operation(summary = "Create a new tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tag created successfully", content = @Content(schema = @Schema(implementation = Tag.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflict", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    Tag createTag(String name);

    @Operation(summary = "Get all tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags retrieved successfully", content = @Content(schema = @Schema(implementation = Tag.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    List<Tag> getTags();

    @Operation(summary = "Get tag by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tag retrieved successfully", content = @Content(schema = @Schema(implementation = Tag.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    Tag getTag(Long id);

    @Operation(summary = "Delete tag by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tag deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> deleteTag(Long id);

}
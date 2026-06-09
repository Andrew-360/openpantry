package com.communityPantry.communityPantry.web.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.communityPantry.communityPantry.dto.community.CommunityDTO;
import com.communityPantry.communityPantry.dto.community.CreateCommunityRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.communityPantry.communityPantry.exception.ApiError;

@Tag(name = "Community", description = "Endpoints for managing communities")
public interface CommunityControllerInterface {

    // Create a new community
    @Operation(summary = "Create a new community")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Community created successfully", content = @Content(schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflict", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<CommunityDTO> createCommunity(Authentication authentication, CreateCommunityRequest request);

    // Delete a community
    @Operation(summary = "Delete a community by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Community deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Community not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Void> deleteCommunity(Long id);

    // Get all communities in a location
    @Operation(summary = "Get all communities in a location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Communities retrieved successfully", content = @Content(schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Page<CommunityDTO>> getAllCommunitiesInLocation(
            String location,
            Pageable pageable);

    // Get a community by ID
    @Operation(summary = "Get a community by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community retrieved successfully", content = @Content(schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "404", description = "Community not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<CommunityDTO> getCommunityById(Long id);

    // Update a community
    @Operation(summary = "Update a community by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Community updated successfully", content = @Content(schema = @Schema(implementation = CommunityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Community not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflict", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<CommunityDTO> updateCommunity(
            Long id,
            CreateCommunityRequest request);

}
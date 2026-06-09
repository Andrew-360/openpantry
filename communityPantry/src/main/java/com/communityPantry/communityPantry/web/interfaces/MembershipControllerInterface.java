package com.communityPantry.communityPantry.web.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.dto.membership.MembershipResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.communityPantry.communityPantry.exception.ApiError;

@Tag(name = "Membership", description = "Endpoints for managing community memberships")
public interface MembershipControllerInterface {

    // Create a new membership for the current user
    @Operation(summary = "Create a new membership for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Membership created successfully", content = @Content(schema = @Schema(implementation = Membership.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflict", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<MembershipResponse> createMembership(Authentication authentication, Long communityId);

    // Update a current membership.
    @Operation(summary = "Update a membership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership updated successfully", content = @Content(schema = @Schema(implementation = Membership.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Membership not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflict", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<MembershipResponse> updateMembership(
            Authentication authentication,
            Long communityId,
            Long userProfileId,
            Map<String, Object> membershipDetails);

    // Delete a membership
    @Operation(summary = "Delete a membership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Membership deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Membership not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<String> deleteMembership(Authentication authentication, Long communityId, Long userProfileId);

    @Operation(summary = "Leave a community")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Membership deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Membership not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<String> leaveCommunity(Authentication authentication, Long communityId);

    // Get all memberships for a community
    @Operation(summary = "Get all memberships for a community")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Memberships retrieved successfully", content = @Content(schema = @Schema(implementation = Membership.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<MembershipResponse>> getMembershipsByCommunity(Long communityId);

    // Get all memberships for a user
    @Operation(summary = "Get all memberships for a user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Memberships retrieved successfully", content = @Content(schema = @Schema(implementation = MembershipResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<MembershipResponse>> getMembershipsByUserProfile(Long userProfileId);

    // Get a membership for a given community and userprofile
    @Operation(summary = "Get a membership by community and user profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membership retrieved successfully", content = @Content(schema = @Schema(implementation = MembershipResponse.class))),
            @ApiResponse(responseCode = "404", description = "Membership not found", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<MembershipResponse> getMembershipByCommunityAndUserProfile(Long communityId,
            Long userProfileId);

}
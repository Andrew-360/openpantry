package com.communityPantry.communityPantry.web.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.communityPantry.communityPantry.dto.fooditem.FoodItemRequest;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.communityPantry.communityPantry.exception.ApiError;

@Tag(name = "Food Item", description = "Endpoints for managing food items")
public interface FoodItemControllerInterface {

    /**
     * posting endpoint: POST /food-items
     * logged in user creates a food item/listing via the service layer
     */
    @Operation(summary = "Create a new food item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Food item created successfully", content = @Content(schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT required", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate or conflict", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<FoodItemResponse> handleCreateFoodItem(Authentication authentication,
            FoodItemRequest request);

    /**
     * Get /food-items
     * returns all food items
     */
    @Operation(summary = "Get all food items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food items retrieved successfully", content = @Content(schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<FoodItemResponse>> handleGetAllFoodItems();

    /**
     * Get /food-items/search?name=chips
     * returns food items with a specified name
     */
    @Operation(summary = "Search food items by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food items retrieved successfully", content = @Content(schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<FoodItemResponse>> handleSearchFoodItemsByName(String name);

    /**
     * Get /food-items/tag?name=vegetarian
     * returns food items with specified tags
     */
    @Operation(summary = "Search food items by tags")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Food items retrieved successfully", content = @Content(schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid argument", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<Set<FoodItemResponse>> handleSearchFoodItemsByTags(Set<Long> tag_ids);

    /**
     * Get /food-items/available
     * returns available food items
     */
    @Operation(summary = "Get available food items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available food items retrieved successfully", content = @Content(schema = @Schema(implementation = FoodItemResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    ResponseEntity<List<FoodItemResponse>> handleGetAvailableFoodItems();

}
package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.dto.fooditem.FoodItemRequest;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemResponse;
import com.communityPantry.communityPantry.service.FoodItemService;
import com.communityPantry.communityPantry.web.interfaces.FoodItemControllerInterface;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.List;

/**
 * Handles HTTP endpoints for the frontend
 * Controller receives requests, passes work to service and
 * returns response DTOs.
 */

@RestController // returns JSON responses
@RequestMapping("/food-items")
public class FoodItemController implements FoodItemControllerInterface {

    private final FoodItemService food_item_service;

    public FoodItemController(FoodItemService food_item_service) {
        this.food_item_service = food_item_service;
    }

    /**
     * posting endpoint: POST /food-items
     * logged in user creates a food item/listing via the service layer
     */
    @Override
    @PostMapping // base route handling POST
    public ResponseEntity<FoodItemResponse> handleCreateFoodItem(Authentication authentication,
            @RequestBody @Valid FoodItemRequest request) {
        String username = authentication.getName();
        return ResponseEntity.ok(this.food_item_service.createFoodItem(username, request));
    }

    /**
     * Get /food-items
     * returns all food items
     */
    @Override
    @GetMapping // base route handling GET
    public ResponseEntity<List<FoodItemResponse>> handleGetAllFoodItems() {
        return ResponseEntity.ok(this.food_item_service.getAllFoodItems());
    }

    /**
     * Get /food-items/search?name=chips
     * returns food items with a specified name
     */
    @Override
    @GetMapping("/search") // base route handling GET
    public ResponseEntity<List<FoodItemResponse>> handleSearchFoodItemsByName(@RequestParam String name) {
        return ResponseEntity.ok(this.food_item_service.searchFoodItemsByName(name));
    }

    /**
     * Get /food-items/tag?name=vegetarian
     * returns food items with specified tags
     */
    @Override
    @GetMapping("/tag") // base route handling GET
    public ResponseEntity<Set<FoodItemResponse>> handleSearchFoodItemsByTags(@RequestParam Set<Long> tag_ids) {
        // String username = authentication.getName();
        return ResponseEntity.ok(this.food_item_service.searchFoodItemsByTagIds(tag_ids));
    }

    /**
     * Get /food-items/available
     * returns available food items
     */
    @Override
    @GetMapping("/available") // base route handling GET
    public ResponseEntity<List<FoodItemResponse>> handleGetAvailableFoodItems() {
        return ResponseEntity.ok(this.food_item_service.getAvailableFoodItems());
    }

    /**
     * Get /food-items/by-date?expiry_date=2026-03-28
     * returns food items that won't be expired on a specified date
     */
    // @GetMapping("/{expiry_date}") // base route handling GET
    // public List<FoodItemResponse> handleGetFoodItemsByDate(@RequestParam
    // LocalDate expiry_date) {
    // // revisit this: the request DTO may not have the same deadline set as its
    // // expiry date
    // return this.food_item_service.handleGetFoodItemsByDate(expiry_date);
    // }
}

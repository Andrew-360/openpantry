package com.communityPantry.communityPantry.mapper;

import com.communityPantry.communityPantry.dto.fooditem.FoodItemRequest;
import com.communityPantry.communityPantry.dto.fooditem.FoodItemResponse;
import com.communityPantry.communityPantry.domain.FoodItem;
import com.communityPantry.communityPantry.domain.Tag;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Responsible for mapping between database entities and request/response DTOs
 */
@Component
public class FoodItemMapper {
    // converts a food item entity into a food item response
    public FoodItemResponse toResponse(FoodItem food_item) {
        FoodItemResponse response = new FoodItemResponse();

        // parse data from the food item entity to the response DTO
        response.setId(food_item.getId());
        response.setName(food_item.getName());
        response.setDescription(food_item.getDescription());
        response.setQuantity(food_item.getQuantity());
        response.setExpiry(food_item.getExpiry());
        response.setStatus(food_item.getStatus());
        response.setImage(food_item.getImage());

        if (food_item.getUserProfile() != null) {
            response.setUserProfileId(food_item.getUserProfile().getId());
        }

        if (food_item.getCommunity() != null) {
            response.setCommunityId(food_item.getCommunity().getId());
        }

        response.setTagIds(
                food_item.getTags() // gets the set of tags(Tags) from entity
                        .stream() // converts set to a stream
                        .map(Tag::getId) // transforms each tag(Tag) to its id(Long)
                        .collect(Collectors.toSet()) // gathers results in a set of ids(Long)
        );

        return response;
    }

    // converts a food item request into a food item entity
    public FoodItem toEntity(FoodItemRequest request) {
        FoodItem food_item = new FoodItem();

        // parse data from the request DTO to the food item entity
        food_item.setName(request.getName());
        food_item.setDescription(request.getDescription());
        food_item.setQuantity(request.getQuantity());
        food_item.setExpiry(request.getExpiry());
        food_item.setImage(request.getImage());

        // request DTO can't parse community, user profile, status
        // this will be done in service

        return food_item;
    }
}

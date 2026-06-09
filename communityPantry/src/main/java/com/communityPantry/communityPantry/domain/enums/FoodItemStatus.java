package com.communityPantry.communityPantry.domain.enums;

/**
 * Represents the status indicators for food items
 */
public enum FoodItemStatus {

    // food item can be reserved by recipients
    AVAILABLE,
    // food item has been reserved by a recipient
    RESERVED,
    // food item has expired
    EXPIRED
}

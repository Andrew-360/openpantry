package com.communityPantry.communityPantry.dto.Reservation;

import jakarta.validation.constraints.NotNull;

public class ReservationRequest {
    @NotNull(message = "User Profile ID is required")
    private long userProfileId;

    @NotNull(message = "Food Item ID is required")
    private long foodItemId;

    // the reservation date automatically set to current date so not required in request


    // the reservation status automatically set to PENDING so not required in request
    @NotNull(message = "Quantity is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    // Getters and Setters

    public long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

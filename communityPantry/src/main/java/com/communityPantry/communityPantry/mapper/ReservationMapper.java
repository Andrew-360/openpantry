package com.communityPantry.communityPantry.mapper;

import org.springframework.stereotype.Component;
import com.communityPantry.communityPantry.domain.Reservation;
import com.communityPantry.communityPantry.dto.Reservation.ReservationRequest;
import com.communityPantry.communityPantry.dto.Reservation.ReservationResponse;

@Component
public class ReservationMapper {
    // maps a Reservation entity to a ReservationResponse
    public ReservationResponse toDTO(Reservation reservation) {
        ReservationResponse dto = new ReservationResponse();
        dto.setId(reservation.getId());
        dto.setUsername(reservation.getUserProfile().getUser().getUsername());
        dto.setUserProfileId(reservation.getUserProfile().getId());
        dto.setFoodItemId(reservation.getFoodItem().getId());
        dto.setFoodItemName(reservation.getFoodItem().getName());
        dto.setQuantity(reservation.getQuantity());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setReservationStatus(reservation.getReservationStatus());
        return dto;
    }
    //maps a ReservationRequest to a Reservation entity
    //date, status, foodItem and userProfile is set in service layer
    public Reservation toEntity(ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setQuantity(request.getQuantity());
        return reservation;
    }
}

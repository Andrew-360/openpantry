package com.communityPantry.communityPantry.web.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.communityPantry.communityPantry.dto.Reservation.ReservationRequest;
import com.communityPantry.communityPantry.dto.Reservation.ReservationResponse;
import com.communityPantry.communityPantry.service.ReservationService;
import com.communityPantry.communityPantry.service.UserService;
import com.communityPantry.communityPantry.web.interfaces.ReservationControllerInterface;
import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController implements ReservationControllerInterface {
    ReservationService reservationService;
    UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    // get reservations by id
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        ReservationResponse response = reservationService.getReservationById(id);
        return ResponseEntity.ok(response);
    }

    // create a reservation
    @Override
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse created = reservationService.createReservation(request);
        return ResponseEntity.ok(created);
    }

    // Cancel a reservation
    // needs reservationId and username
    @Override
    @PatchMapping("/{reservationId}/cancellation")
    public ResponseEntity<ReservationResponse> cancelReservation(Authentication authentication,
            @PathVariable Long reservationId) {
        String username = authentication.getName();
        ReservationResponse response = reservationService.cancelReservation(reservationId, username);
        return ResponseEntity.ok(response);
    }

    // get reservations by username
    @Override
    @GetMapping("/user")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUsername(Authentication authentication) {
        String username = authentication.getName();
        List<ReservationResponse> responses = reservationService.getReservationsByUsername(username);
        return ResponseEntity.ok(responses);
    }

    // get reservations by foodItem id
    @Override
    @GetMapping("/food/{foodItemId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByFoodItemId(@PathVariable Long foodItemId) {
        List<ReservationResponse> responses = reservationService.getReservationsByFoodItemId(foodItemId);
        return ResponseEntity.ok(responses);
    }

    // get reservations by reservation status
    @Override
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByReservationStatus(
            @PathVariable ReservationStatus status) {
        List<ReservationResponse> responses = reservationService.getReservationsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    // get reservations by username and reservation status
    @Override
    @GetMapping("/user/status/{status}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUsernameAndReservationStatus(
            Authentication authentication, @PathVariable ReservationStatus status) {
        String username = authentication.getName();
        List<ReservationResponse> responses = reservationService.getReservationsByUsernameAndStatus(username,
                status);
        return ResponseEntity.ok(responses);
    }

    // complete reservations
    // needs reservationId and username
    @Override
    @PatchMapping("/{reservationId}/completion")
    public ResponseEntity<ReservationResponse> completeReservation(@PathVariable Long reservationId,
            Authentication authentication) {
        String username = authentication.getName();
        ReservationResponse response = reservationService.completeReservation(reservationId, username);
        return ResponseEntity.ok(response);
    }

}

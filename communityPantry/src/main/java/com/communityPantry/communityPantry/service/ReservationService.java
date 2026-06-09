package com.communityPantry.communityPantry.service;

import org.springframework.stereotype.Service;

import com.communityPantry.communityPantry.repository.ReservationRepository;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;

import jakarta.transaction.Transactional;

import com.communityPantry.communityPantry.mapper.ReservationMapper;
import com.communityPantry.communityPantry.dto.Reservation.ReservationResponse;
import com.communityPantry.communityPantry.dto.Reservation.ReservationRequest;
import com.communityPantry.communityPantry.domain.Reservation;
import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.domain.FoodItem;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.exception.AlreadyExpiredException;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.exception.InsufficientQuantityException;
import com.communityPantry.communityPantry.exception.InvalidStatusException;
import com.communityPantry.communityPantry.exception.AlreadyReservedException;
import com.communityPantry.communityPantry.exception.InvalidCredentialsException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Transactional
@Service
public class ReservationService {
    // handles the business logic of reservations
    // need to update foodItem and userProfile so will be added later

    // the repositories required for the service layer
    private final ReservationRepository reservationRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserProfileRepository userProfileRepository;
    private final ReservationMapper reservationMapper;

    // dependency injection
    public ReservationService(ReservationRepository reservationRepository, FoodItemRepository foodItemRepository,
            UserProfileRepository userProfileRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.foodItemRepository = foodItemRepository;
        this.userProfileRepository = userProfileRepository;
        this.reservationMapper = reservationMapper;
    }

    // Function to create a reservation
    public ReservationResponse createReservation(ReservationRequest reservationRequest) {
        // check if the foodId exists in the repository using its id
        FoodItem foodItem = foodItemRepository.findById(reservationRequest.getFoodItemId()).orElseThrow(
                () -> new EntityNotFoundException("Food item not found with id: " + reservationRequest.getFoodItemId()));
        // check if the reservation quantity is greater than 0 (can't reserve 0 items)
        if (reservationRequest.getQuantity() < 1) {
            throw new InsufficientQuantityException(
                    "You must reserve at least one food item with id: " + reservationRequest.getFoodItemId());
        }
        // check if the reserved quantity is less than the foodItem quantity left
        if (foodItem.getQuantity() < reservationRequest.getQuantity()) {
            throw new InsufficientQuantityException(
                    "Not enough quantity available for food item with id: " + reservationRequest.getFoodItemId());
        }
        // check if the foodItem is expired (can't reserved expired food)
        if (foodItem.getExpiry().isBefore(LocalDate.now())) {
            throw new AlreadyExpiredException(
                    "Cannot reserve expired food item with id: " + reservationRequest.getFoodItemId());
        }
        // check if the foodItem is not already fully reserved
        if (foodItem.getStatus() == FoodItemStatus.RESERVED) {
            throw new AlreadyReservedException(
                    "Cannot reserve unavailable food item with id: " + reservationRequest.getFoodItemId());
        }
        // check if the userProfile reserving exists in the repository using its id
        UserProfile userProfile = userProfileRepository.findById(reservationRequest.getUserProfileId()).orElseThrow(
                () -> new EntityNotFoundException("User profile not found with id: " + reservationRequest.getUserProfileId()));
        // map the request to an entity
        Reservation reservation = reservationMapper.toEntity(reservationRequest);
        // set reservation date, status, foodItem and userProfile
        reservation.setReservationDate(LocalDate.now());
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setFoodItem(foodItem);
        reservation.setUserProfile(userProfile);
        // save reservation to the repository
        Reservation savedReservation = reservationRepository.save(reservation);
        // update food item quantity to new amount
        foodItem.setQuantity(foodItem.getQuantity() - reservationRequest.getQuantity());
        if (foodItem.getQuantity() == 0) {
            foodItem.setStatus(FoodItemStatus.RESERVED);
        }
        // save updated foodItem to repository
        foodItemRepository.save(foodItem);
        return reservationMapper.toDTO(savedReservation);
    }

    // Function to cancel reservations
    public ReservationResponse cancelReservation(Long reservationId, String username) {
        // get userProfileId from username
        UserProfile userProfile = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        Long userProfileId = userProfile.getId();

        // check if the reservation exists in repository using its id
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));
        // check if the reservation's userId matches the userId of the cancellation
        // request
        if (!reservation.getUserProfile().getId().equals(userProfileId)) {
            throw new InvalidCredentialsException("Reservation does not belong to user with id: " + userProfileId);
        }
        // check if the reservation is already cancelled
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidStatusException("Reservation with id: " + reservationId + " is already cancelled");
        }
        // check if the reservation is already completed
        if (reservation.getReservationStatus() == ReservationStatus.COMPLETED) {
            throw new InvalidStatusException(
                    "Reservation with id: " + reservationId + " is already completed and cannot be cancelled");
        }
        // complete the cancellation
        // change status and save to repository
        reservation.setReservationStatus(ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);
        // update food item quantity to pre-reserved amount
        FoodItem foodItem = reservation.getFoodItem();
        foodItem.setQuantity(foodItem.getQuantity() + reservation.getQuantity());
        // set foodItem back to available if its quantity is greater than zero and not expired
        if (foodItem.getQuantity() > 0) {
            if (foodItem.getExpiry().isBefore(LocalDate.now())) {
                foodItem.setStatus(FoodItemStatus.EXPIRED);
            } else {
                foodItem.setStatus(FoodItemStatus.AVAILABLE);
            }
        }
        // save foodItem to repository
        foodItemRepository.save(foodItem);
        return reservationMapper.toDTO(cancelledReservation);
    }

    // complete reservation
    public ReservationResponse completeReservation(Long reservationId, String username) {
        // get userProfileId from username
        UserProfile userProfile = userProfileRepository.findByUserUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        Long userProfileId = userProfile.getId();
        // check if reservation exists in the repository using its id
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));
        // check if the reservation's userId matches the userId of the completion request
        if (!reservation.getUserProfile().getId().equals(userProfileId)) {
            throw new InvalidCredentialsException("Reservation does not belong to user with id: " + userProfileId);
        }
        // check if the reservation is already completed
        if (reservation.getReservationStatus() == ReservationStatus.COMPLETED) {
            throw new InvalidStatusException("Reservation with id: " + reservationId + " is already completed");
        }
        // check if the reservation is cancelled
        if (reservation.getReservationStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidStatusException(
                    "Reservation with id: " + reservationId + " is already cancelled and cannot be completed");
        }
        // check if the reservationStatus is pending
        if (reservation.getReservationStatus() != ReservationStatus.PENDING) {
            throw new InvalidStatusException("Only pending reservations can be completed");
        }
        // check if the foodItem has expired
        FoodItem foodItem = reservation.getFoodItem();
        if (FoodItemStatus.EXPIRED == foodItem.getStatus() || foodItem.getExpiry().isBefore(LocalDate.now())) {
            throw new AlreadyExpiredException(
                    "Cannot complete reservation for an expired food item with id: " + foodItem.getId());
        }
        // complete the reservation and set the reservationStatus to completed
        reservation.setReservationStatus(ReservationStatus.COMPLETED);
        // save the completed reservation to the repository
        Reservation completedReservation = reservationRepository.save(reservation);
        return reservationMapper.toDTO(completedReservation);
    }

    // get reservation by id
    public ReservationResponse getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));
        return reservationMapper.toDTO(reservation);
    }

    // Function to get reservations by username
    public List<ReservationResponse> getReservationsByUsername(String username) {
        List<Reservation> reservations = reservationRepository.findByUserProfileUserUsername(username);
        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
        return reservationResponses;
    }

    // Function to get reservations by foodId
    public List<ReservationResponse> getReservationsByFoodItemId(Long foodItemId) {
        // check if foodItem exists in repository
        foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + foodItemId));
        List<Reservation> reservations = reservationRepository.findByFoodItemId(foodItemId);
        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
        return reservationResponses;
    }

    // Function get reservations by their status
    public List<ReservationResponse> getReservationsByStatus(ReservationStatus reservationStatus) {

        List<Reservation> reservations = reservationRepository.findByReservationStatus(reservationStatus);

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());

        return reservationResponses;
    }

    // Function to get reservations by their date
    public List<ReservationResponse> getReservationsByDate(LocalDate reservationDate) {

        List<Reservation> reservations = reservationRepository.findByReservationDate(reservationDate);

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());

        return reservationResponses;
    }

    // Function to get reservations by their username and status
    public List<ReservationResponse> getReservationsByUsernameAndStatus(String username,
            ReservationStatus reservationStatus) {
        List<Reservation> reservations = reservationRepository.findByUserProfileUserUsernameAndReservationStatus(username,
                reservationStatus);

        List<ReservationResponse> reservationResponses = reservations.stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());

        return reservationResponses;
    }

}

package com.communityPantry.communityPantry.service;


import com.communityPantry.communityPantry.domain.FoodItem;
import com.communityPantry.communityPantry.domain.Reservation;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.enums.FoodItemStatus;
import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import com.communityPantry.communityPantry.dto.Reservation.ReservationRequest;
import com.communityPantry.communityPantry.dto.Reservation.ReservationResponse;
import com.communityPantry.communityPantry.mapper.ReservationMapper;
import com.communityPantry.communityPantry.repository.FoodItemRepository;
import com.communityPantry.communityPantry.repository.ReservationRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.exception.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceUnitTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserProfileRepository userProfileRepository;
    @Mock
    private ReservationMapper reservationMapper;
    @InjectMocks
    private ReservationService reservationService;

    //Create Tests
    //test to see if exception is thrown if the quantity reserved is more than quantity available
    @Test
    void ifNotEnoughQuantityShouldThrowException() {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(1L);
        foodItem.setQuantity(2);
        foodItem.setExpiry(LocalDate.now().plusDays(2));
        //simulate finding the foodItem by id
        Mockito.when(foodItemRepository.findById(1L)).thenReturn(Optional.of(foodItem));

        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(1L);
        request.setQuantity(5);

        InsufficientQuantityException exception = assertThrows(InsufficientQuantityException.class, () -> {
            reservationService.createReservation(request);
        });
        assertEquals("Not enough quantity available for food item with id: 1", exception.getMessage());
    }
    //test to see if exception is thrown if the reservationID doesnt match foodID
    @Test
    void ifWrongFoodIdShouldThrowException() {
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(1L);
        request.setQuantity(1);
        //simulate not finding the foodItem by id
        Mockito.when(foodItemRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            reservationService.createReservation(request);
        });
        assertEquals("Food item not found with id: 1", exception.getMessage());
    }
    //test to see if exception is thrown if the reserved quantity is less than 1
    @Test
    void ifQuantityLessThanOneShouldThrowException() {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(3L);
        foodItem.setQuantity(2);
        foodItem.setExpiry(LocalDate.now().plusDays(2));

        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(3L);
        request.setQuantity(0);
        //simulate finding the foodItem by id
        Mockito.when(foodItemRepository.findById(3L)).thenReturn(Optional.of(foodItem));
        InsufficientQuantityException exception = assertThrows(InsufficientQuantityException.class, () -> {
            reservationService.createReservation(request);
        });
        assertEquals("You must reserve at least one food item with id: 3", exception.getMessage());
    }
    //test to see if exception is thrown if the food is expired(food expires day after expiry date)
    @Test
    void ifExpiredShouldThrowException() {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(4L);
        foodItem.setQuantity(2);
        foodItem.setExpiry(LocalDate.now().minusDays(2));

        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(4L);
        request.setQuantity(1);
        //simulate finding the foodItem by id
        Mockito.when(foodItemRepository.findById(4L)).thenReturn(Optional.of(foodItem));
        AlreadyExpiredException exception = assertThrows(AlreadyExpiredException.class, () -> {
            reservationService.createReservation(request);
        });
        assertEquals("Cannot reserve expired food item with id: 4", exception.getMessage());
    }
    //test to see if exception is thrown if the food is expired(food expires day after expiry date)
    @Test
    void ifReservedShouldThrowException() {
        FoodItem foodItem = new FoodItem();
        foodItem.setId(5L);
        foodItem.setQuantity(2);
        foodItem.setExpiry(LocalDate.now().plusDays(2));
        foodItem.setStatus(FoodItemStatus.RESERVED);

        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(5L);
        request.setQuantity(1);
        //simulate finding the foodItem by id
        Mockito.when(foodItemRepository.findById(5L)).thenReturn(Optional.of(foodItem));
        AlreadyReservedException exception = assertThrows(AlreadyReservedException.class, () -> {
            reservationService.createReservation(request);
        });
        assertEquals("Cannot reserve unavailable food item with id: 5", exception.getMessage());
    }
    //test to see if a reservation is created if all fields match the right conditions
    @Test
    void createReservationTest() {
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(6L);
        foodItem.setQuantity(2);
        foodItem.setExpiry(LocalDate.now().plusDays(2));
        //create userProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create a request to reserve
        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(6L);
        request.setQuantity(1);

        Reservation reservation = new Reservation();
        ReservationResponse reservationResponse = new ReservationResponse();
        //simulate finding the foodItem by id
        Mockito.when(foodItemRepository.findById(6L)).thenReturn(Optional.of(foodItem));
        //simulate finding the userProfile by id
        Mockito.when(userProfileRepository.findById(0L)).thenReturn(Optional.of(userProfile));
        //simulate converting the request to an entity
        Mockito.when(reservationMapper.toEntity(request)).thenReturn(reservation);
        //simulate saving the entity to the repository
        Mockito.when(reservationRepository.save(reservation)).thenReturn(reservation);
        //simulate converting the entity to a response
        Mockito.when (reservationMapper.toDTO(reservation)).thenReturn(reservationResponse);
        //create the reservation
        ReservationResponse result = reservationService.createReservation(request);
        assertNotNull(result);

        verify(reservationRepository).save(reservation);
        verify(foodItemRepository).save(foodItem);
    }
    //tests for cancelService
    //test to see if exception is thrown if a user tries to cancel another user's reservation
    @Test
    void ifWrongUserProfileShouldThrowException() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        UserProfile userProfile1 = new UserProfile();
        userProfile1.setId(1L);
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(7L);

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("1")).thenReturn(Optional.of(userProfile1));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(7L)).thenReturn(Optional.of(reservation));
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            reservationService.cancelReservation(7L, "1");
        });
        assertEquals("Reservation does not belong to user with id: 1", exception.getMessage());
    }
    //test to see if exception is thrown if the food status is cancelled
    @Test
    void ifAlreadyCancelledShouldThrowException() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(8L);
        reservation.setReservationStatus(ReservationStatus.CANCELLED);

        //simulate finding the userProfile by username then returning id
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(8L)).thenReturn(Optional.of(reservation));
        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> {
            reservationService.cancelReservation(8L, "0");
        });
        assertEquals("Reservation with id: 8 is already cancelled", exception.getMessage());
    }

    //test to see if exception is thrown if the reservation is already completed
    @Test
    void ifAlreadyCompletedShouldThrowExceptionInCancel() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(8L);
        reservation.setReservationStatus(ReservationStatus.COMPLETED);

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(8L)).thenReturn(Optional.of(reservation));
        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> {
            reservationService.cancelReservation(8L, "0");
        });
        assertEquals("Reservation with id: 8 is already completed and cannot be cancelled", exception.getMessage());
    }
    //test to see if a reservation is cancelled if all fields match the right conditions
    @Test
    void cancelReservationTest() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(9L);
        foodItem.setQuantity(2);
        foodItem.setExpiry(LocalDate.now().plusDays(1));
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(9L);
        reservation.setFoodItem(foodItem);
        reservation.setQuantity(2);
        //create response
        ReservationResponse response = new ReservationResponse();

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));
        //simulate saving the entity to the repository
        Mockito.when(reservationRepository.save(reservation)).thenReturn(reservation);
        //simulate converting the entity to a response
        Mockito.when(reservationMapper.toDTO(reservation)).thenReturn(response);
        //cancel the reservation
        ReservationResponse result = reservationService.cancelReservation(9L, "0");

        assertNotNull(result);
        verify(reservationRepository).save(reservation);
        verify(foodItemRepository).save(foodItem);
    }
    //test to see if a reservation is cancelled if all fields match the right conditions
    @Test
    void cancelReservationIncreasesQuantity() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(9L);
        foodItem.setQuantity(0);
        foodItem.setExpiry(LocalDate.now().plusDays(1));
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(9L);
        reservation.setFoodItem(foodItem);
        reservation.setQuantity(2);
        //create response
        ReservationResponse response = new ReservationResponse();

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));
        //simulate saving the entity to the repository
        Mockito.when(reservationRepository.save(reservation)).thenReturn(reservation);
        //simulate converting the entity to a response
        Mockito.when(reservationMapper.toDTO(reservation)).thenReturn(response);
        //cancel the reservation
        ReservationResponse result = reservationService.cancelReservation(9L, "0");

        assertEquals(2, foodItem.getQuantity());
        verify(reservationRepository).save(reservation);
        verify(foodItemRepository).save(foodItem);
    }
    //test to see if cancelling a reservation changes the status to cancelled
    @Test
    void cancelReservationChangesStatus() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(9L);
        foodItem.setQuantity(0);
        foodItem.setExpiry(LocalDate.now().plusDays(1));
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(9L);
        reservation.setFoodItem(foodItem);
        reservation.setQuantity(2);
        //create response
        ReservationResponse response = new ReservationResponse();

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));
        //simulate saving the entity to the repository
        Mockito.when(reservationRepository.save(reservation)).thenReturn(reservation);
        //simulate converting the entity to a response
        Mockito.when(reservationMapper.toDTO(reservation)).thenReturn(response);
        //cancel the reservation
        ReservationResponse result = reservationService.cancelReservation(9L, "0");

        assertEquals(FoodItemStatus.AVAILABLE, foodItem.getStatus());
        assertEquals(ReservationStatus.CANCELLED, reservation.getReservationStatus());
        verify(reservationRepository).save(reservation);
        verify(foodItemRepository).save(foodItem);
    }
//test to see if cancelling an expired reservation sets foodItem status to expired
    @Test
    void cancelReservationSetsExpiredStatus() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(9L);
        foodItem.setQuantity(0);
        foodItem.setExpiry(LocalDate.now().minusDays(1)); // Expired
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(9L);
        reservation.setFoodItem(foodItem);
        reservation.setQuantity(2);
        //create response
        ReservationResponse response = new ReservationResponse();

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));
        //simulate saving the entity to the repository
        Mockito.when(reservationRepository.save(reservation)).thenReturn(reservation);
        //simulate converting the entity to a response
        Mockito.when(reservationMapper.toDTO(reservation)).thenReturn(response);
        //cancel the reservation
        ReservationResponse result = reservationService.cancelReservation(9L, "0");

        assertEquals(FoodItemStatus.EXPIRED, foodItem.getStatus());
        verify(foodItemRepository).save(foodItem);
    }
    //tests for complete reservation
    //test to see if exception is thrown if a user tries to complete another user's reservation
    @Test
    void ifWrongUserProfileShould1ThrowException() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        UserProfile userProfile1 = new UserProfile();
        userProfile1.setId(1L);
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(7L);

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("1")).thenReturn(Optional.of(userProfile1));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(7L)).thenReturn(Optional.of(reservation));
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> {
            reservationService.completeReservation(7L, "1");
        });
        assertEquals("Reservation does not belong to user with id: 1", exception.getMessage());
    }
    //test to see if exception is thrown if the reservation status is completed
    @Test
    void ifAlreadyCompletedShouldThrowException() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(8L);
        reservation.setReservationStatus(ReservationStatus.COMPLETED);

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(8L)).thenReturn(Optional.of(reservation));
        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> {
            reservationService.completeReservation(8L, "0");
        });
        assertEquals("Reservation with id: 8 is already completed", exception.getMessage());
    }

    //test to see if exception is thrown if reservation is already cancelled
    @Test
    void ifAlreadyCancelledShouldThrowExceptionInComplete() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(8L);
        reservation.setReservationStatus(ReservationStatus.CANCELLED);

        //simulate finding the userProfile by username then returning id
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(8L)).thenReturn(Optional.of(reservation));
        InvalidStatusException exception = assertThrows(InvalidStatusException.class, () -> {
            reservationService.completeReservation(8L, "0");
        });
        assertEquals("Reservation with id: 8 is already cancelled and cannot be completed", exception.getMessage());
    }
    //test to see if exception is thrown if a reservation's food item is expired
    @Test
    void ifFoodItemExpiredShouldThrowExceptionInComplete() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(9L);
        foodItem.setExpiry(LocalDate.now().minusDays(1));
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setId(8L);
        reservation.setFoodItem(foodItem);
        reservation.setReservationStatus(ReservationStatus.PENDING);

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(8L)).thenReturn(Optional.of(reservation));
        AlreadyExpiredException exception = assertThrows(AlreadyExpiredException.class, () -> {
            reservationService.completeReservation(8L, "0");
        });
        assertEquals("Cannot complete reservation for an expired food item with id: 9", exception.getMessage());
    }
    //test to see if a reservation is completed if all fields match the right conditions
    @Test
    void completeReservationTest() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);
        //create foodItem
        FoodItem foodItem = new FoodItem();
        foodItem.setId(9L);
        foodItem.setQuantity(0);
        foodItem.setStatus(FoodItemStatus.RESERVED);
        foodItem.setExpiry(LocalDate.now().plusDays(1));
        //create reservation
        Reservation reservation = new Reservation();
        reservation.setUserProfile(userProfile);
        reservation.setReservationStatus(ReservationStatus.PENDING);
        reservation.setId(9L);
        reservation.setFoodItem(foodItem);
        reservation.setQuantity(2);
        //create response
        ReservationResponse response = new ReservationResponse();

        //simulate finding the userProfile by username
        Mockito.when(userProfileRepository.findByUserUsername("0")).thenReturn(Optional.of(userProfile));
        //simulate finding the reservation by id
        Mockito.when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));
        //simulate saving the entity to the repository
        Mockito.when(reservationRepository.save(reservation)).thenReturn(reservation);
        //simulate converting the entity to a response
        Mockito.when(reservationMapper.toDTO(reservation)).thenReturn(response);
        //complete the reservation
        ReservationResponse result = reservationService.completeReservation(9L, "0");

        assertEquals(FoodItemStatus.RESERVED, foodItem.getStatus());
        assertEquals(ReservationStatus.COMPLETED, reservation.getReservationStatus());
        verify(reservationRepository).save(reservation);
    }
    //tests for get methods
    @Test
    void getReservationsByUsernameAndStatusTest() {
        //create UserProfile
        UserProfile userProfile = new UserProfile();
        userProfile.setId(0L);

        Mockito.when(reservationRepository.findByUserProfileUserUsernameAndReservationStatus("user", ReservationStatus.PENDING)).thenReturn(java.util.List.of());

        java.util.List<ReservationResponse> results = reservationService.getReservationsByUsernameAndStatus("user", ReservationStatus.PENDING);

        assertNotNull(results);
        verify(reservationRepository).findByUserProfileUserUsernameAndReservationStatus("user", ReservationStatus.PENDING);
    }

    @Test
    void getReservationsByDateTest() {
        LocalDate now = LocalDate.now();
        Mockito.when(reservationRepository.findByReservationDate(now)).thenReturn(java.util.List.of());

        java.util.List<ReservationResponse> results = reservationService.getReservationsByDate(now);

        assertNotNull(results);
        verify(reservationRepository).findByReservationDate(now);
    }

}

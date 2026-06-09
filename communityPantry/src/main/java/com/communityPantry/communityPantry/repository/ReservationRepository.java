package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import com.communityPantry.communityPantry.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    public List<Reservation> findByUserProfileUserUsername(String username);

    public List<Reservation> findByFoodItemId(Long foodItemId);

    public List<Reservation> findByReservationStatus(ReservationStatus reservationStatus);

    public List<Reservation> findByReservationDate(LocalDate reservationDate);

    public List<Reservation> findByUserProfileUserUsernameAndReservationStatus(String username, @NotNull ReservationStatus reservationStatus);

}

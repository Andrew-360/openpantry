package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.enums.ReservationStatus;
import com.communityPantry.communityPantry.dto.Reservation.ReservationRequest;
import com.communityPantry.communityPantry.dto.Reservation.ReservationResponse;
import com.communityPantry.communityPantry.service.ReservationService;
import com.communityPantry.communityPantry.service.UserService;
import com.communityPantry.communityPantry.service.UserDetailsServiceImpl;
import com.communityPantry.communityPantry.security.JwtService;
import com.communityPantry.communityPantry.security.AuthEntryPointJwt;
import org.springframework.web.cors.CorsConfigurationSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(ReservationController.class)
public class ReservationControllerUnitTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    // test to create reservation using api calls
    @Test
    void shouldCreateReservation() throws Exception {

        ReservationRequest request = new ReservationRequest();
        request.setFoodItemId(1L);
        request.setUserProfileId(2L);
        request.setQuantity(3);

        ReservationResponse response = new ReservationResponse();
        response.setId(10L);
        response.setQuantity(3);

        when(reservationService.createReservation(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));

    }

    // test to cancel reservation using api calls
    @Test
    void shouldCancelReservation() throws Exception {

        ReservationResponse response = new ReservationResponse();
        response.setId(40L);

        when(reservationService.cancelReservation(40L, "user"))
                .thenReturn(response);

        mockMvc.perform(patch("/api/reservations/40/cancellation")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(40));

    }

    // test to complete reservation using api calls
    @Test
    void shouldCompleteReservation() throws Exception {

        ReservationResponse response = new ReservationResponse();
        response.setId(30L);

        when(reservationService.completeReservation(30L, "user"))
                .thenReturn(response);

        mockMvc.perform(patch("/api/reservations/30/completion")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(30));

    }

    // test to get reservations by id using api calls
    @Test
    void shouldGetReservationById() throws Exception {

        ReservationResponse response = new ReservationResponse();
        response.setId(25L);

        when(reservationService.getReservationById(25L)).thenReturn(response);

        mockMvc.perform(get("/api/reservations/25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("25"));
    }

    // test to get reservations by user id using api calls
    @Test
    void shouldGetReservationByUsername() throws Exception {

        ReservationResponse response1 = new ReservationResponse();
        response1.setId(24L);

        ReservationResponse response2 = new ReservationResponse();
        response1.setId(23L);

        when(reservationService.getReservationsByUsername("user")).thenReturn(List.of(response1, response2));
        mockMvc.perform(get("/api/reservations/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    //test to get reservations by food id using api call
    @Test
    void shouldGetReservationByFoodId() throws Exception {

        ReservationResponse response1 = new ReservationResponse();
        response1.setId(14L);

        ReservationResponse response2 = new ReservationResponse();
        response1.setId(13L);

        when(reservationService.getReservationsByFoodItemId(78L)).thenReturn(List.of(response1, response2));
        mockMvc.perform(get("/api/reservations/food/78"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    //test to get reservations by user id and status using api call
    @Test
    void shouldGetReservationByUsernameAndStatus() throws Exception {

        ReservationResponse response1 = new ReservationResponse();
        response1.setId(24L);

        ReservationResponse response2 = new ReservationResponse();
        response1.setId(23L);

        // when(userService.getUserIdByUsername("user")).thenReturn(58L);
        when(reservationService.getReservationsByUsernameAndStatus("user", ReservationStatus.PENDING))
                .thenReturn(List.of(response1, response2));
        mockMvc.perform(get("/api/reservations/user/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // test to get reservations by status using api calls
    @Test
    void shouldGetReservationByStatus() throws Exception {

        ReservationResponse response1 = new ReservationResponse();
        response1.setId(14L);

        ReservationResponse response2 = new ReservationResponse();
        response2.setId(13L);

        when(reservationService.getReservationsByStatus(ReservationStatus.PENDING))
                .thenReturn(List.of(response1, response2));
        mockMvc.perform(get("/api/reservations/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}

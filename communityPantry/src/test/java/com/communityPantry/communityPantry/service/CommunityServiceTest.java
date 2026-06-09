package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.dto.community.CommunityDTO;
import com.communityPantry.communityPantry.dto.community.CreateCommunityRequest;
import com.communityPantry.communityPantry.exception.DuplicateEntityException;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.mapper.CommunityMapper;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.MembershipRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CommunityServiceTest {
    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CommunityMapper communityMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private MembershipRepository membershipRepository;  

    @InjectMocks
    private CommunityService communityService;

    // CREATE COMMUNITY TESTS

    // test to check that an exception is thrown when trying to create a community with an existing name and location
    @Test
    void createDuplicateCommunityTest() {
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Test Community");
        request.setLocation("Test Location");

        when(communityRepository.existsByNameAndLocation(request.getName(), request.getLocation())).thenReturn(true);

        DuplicateEntityException exception = assertThrows(DuplicateEntityException.class, () -> {
            communityService.createCommunity("user",request);
        });

        assertEquals("A community with the same name already exists in the this location.", exception.getMessage());
    }

    // test to check if valid community is created successfully
    @Test
    void createValidCommunityTest() {
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Test Community");
        request.setLocation("Test Location");

        Community community = new Community();
        User user = new User();
        user.setId(1L);
        UserProfile userProfile = new UserProfile();


        when(communityRepository.existsByNameAndLocation(request.getName(), request.getLocation())).thenReturn(false);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(userProfile));
        when(communityMapper.toEntity(request)).thenReturn(community);
        when(communityRepository.save(community)).thenReturn(community);
        when(communityMapper.toDTO(community)).thenReturn(new CommunityDTO());

        CommunityDTO result = communityService.createCommunity("user", request);

        assertNotNull(result);
        verify(membershipRepository).save(any(Membership.class));
    }

    // DELETE COMMUNITY TESTS

    // test to check that an exception is thrown when trying to delete a non-existent community
    @Test
    void deleteNonExistentCommunityTest() {
        Long communityId = 1L;

        when(communityRepository.findById(communityId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            communityService.deleteCommunity(communityId);
        });
        assertEquals("Community not found with id: " + communityId, exception.getMessage());
    }
    
    // test to check if community is deleted successfully
    @Test
    void deleteValidCommunityTest() {
        Long communityId = 1L;
        Community community = new Community();

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        communityService.deleteCommunity(communityId);
        verify(communityRepository).delete(community);
    }

    // UPDATE COMMUNITY TESTS

    // test to check that an exception is thrown when trying to update a non-existent community
    @Test
    void updateNonExistentCommunityTest() {
        Long communityId = 1L;
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Updated Community");
        request.setLocation("Updated Location");

        when(communityRepository.findById(communityId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            communityService.updateCommunity(communityId, request);
        });

        assertEquals("Community not found with id: " + communityId, exception.getMessage());
    }

    // test to check that an exception is thrown when trying to update a community with an existing name and location
    @Test
    void updateDuplicateCommunityTest() {
        Long communityId = 1L;
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Updated Community");
        request.setLocation("Updated Location");

        Community community = new Community();
        community.setId(communityId);
        community.setName("Test Community");
        community.setLocation("Test Location");

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityRepository.existsByNameAndLocation(request.getName(), request.getLocation())).thenReturn(true);

        DuplicateEntityException exception = assertThrows(DuplicateEntityException.class, () -> {
            communityService.updateCommunity(communityId, request);
        });

        assertEquals("A community with the same name already exists in the this location.", exception.getMessage());
    }

    // test to check if valid community is updated successfully
     @Test
     void updateValidCommunityTest() {
        Long communityId = 1L;
        CreateCommunityRequest request = new CreateCommunityRequest();
        request.setName("Updated Community");
        request.setLocation("Updated Location");
        request.setDescription("Updated Description");

        Community community = new Community();
        community.setId(communityId);
        community.setName("Test Community");
        community.setLocation("Test Location");
        community.setDescription("Test Description");

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityRepository.existsByNameAndLocation(request.getName(), request.getLocation())).thenReturn(false);
        when(communityRepository.save(community)).thenReturn(community);
        when(communityMapper.toDTO(community)).thenReturn(new CommunityDTO());

        CommunityDTO result = communityService.updateCommunity(communityId, request);
        assertNotNull(result);
    }

    // GET COMMUNITY TESTS
      
    // test to check that an exception is thrown when trying to get a non-existent community by id
    @Test
    void getNonExistentCommunityTest() {
        Long communityId = 1L;

        when(communityRepository.findById(communityId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            communityService.getCommunityById(communityId);
        });

        assertEquals("Community not found with id: " + communityId, exception.getMessage());
    }

    // test to check if valid community is found when getting by id
    @Test
    void getValidCommunityTest() {
        Long communityId = 1L;
        Community community = new Community();

        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(communityMapper.toDTO(community)).thenReturn(new CommunityDTO());
        CommunityDTO result = communityService.getCommunityById(communityId);
        assertNotNull(result);
    }

    // test to check that valid communities are found when getting all communities in a location
    @Test
    void getAllCommunitiesInLocationTest() {
        PageRequest pageable = PageRequest.of(0, 10);
        Community community = new Community();
        Page<Community> communityPage = new PageImpl<>(List.of(community), pageable, 1);

        when(communityRepository.findByLocationContainingIgnoreCase("Test Location", pageable)).thenReturn(communityPage);
        when(communityMapper.toDTO(community)).thenReturn(new CommunityDTO());

        Page<CommunityDTO> result = communityService.getAllCommunitiesInLocation("Test Location", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
    
    // test to check that all communities are returned when location is null
     @Test
     void getAllCommunitiesInNullLocationTest() {
        PageRequest pageable = PageRequest.of(0, 10);
        Community community = new Community();
        Page<Community> communityPage = new PageImpl<>(List.of(community), pageable, 1);

        when(communityRepository.findAll(pageable)).thenReturn(communityPage);
        when(communityMapper.toDTO(community)).thenReturn(new CommunityDTO());

        Page<CommunityDTO> result = communityService.getAllCommunitiesInLocation(null, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
     }
}

package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.domain.UserProfile;
import com.communityPantry.communityPantry.domain.User;
import com.communityPantry.communityPantry.domain.Membership;
import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import com.communityPantry.communityPantry.dto.community.CommunityDTO;
import com.communityPantry.communityPantry.dto.community.CreateCommunityRequest;
import com.communityPantry.communityPantry.exception.DuplicateEntityException;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.mapper.CommunityMapper;
import com.communityPantry.communityPantry.repository.CommunityRepository;
import com.communityPantry.communityPantry.repository.MembershipRepository;
import com.communityPantry.communityPantry.repository.UserProfileRepository;
import com.communityPantry.communityPantry.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final MembershipRepository membershipRepository;

    // Constructor for dependency injection
    public CommunityService(CommunityRepository communityRepository, CommunityMapper communityMapper, UserRepository userRepository, UserProfileRepository userProfileRepository, MembershipRepository membershipRepository) {
        this.communityRepository = communityRepository;
        this.communityMapper = communityMapper;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.membershipRepository = membershipRepository;
    }

    // Create a new community
    public CommunityDTO createCommunity(String username, CreateCommunityRequest request) {
        if (communityRepository.existsByNameAndLocation(request.getName(), request.getLocation())) {
            throw new DuplicateEntityException("A community with the same name already exists in the this location.");
        }
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProfile userProfile = userProfileRepository.findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        Community community = communityMapper.toEntity(request);
        Community savedCommunity = communityRepository.save(community);

        LocalDate joinDate = LocalDate.now();
        Membership membership = Membership.create(CommunityRole.MODERATOR, joinDate, false, savedCommunity, userProfile);
        
        membershipRepository.save(membership);

        return communityMapper.toDTO(savedCommunity);
    }
    
    // Delete a community
    public void deleteCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        List<Membership> memberships = membershipRepository.findByCommunityId(community.getId());
        membershipRepository.deleteAll(memberships);
        communityRepository.delete(community);
    }

    // Get all communities in a location
    @Transactional(readOnly = true)
    public Page<CommunityDTO> getAllCommunitiesInLocation(String location, Pageable pageable) {
        if (location == null || location.isEmpty()) {
            Page<Community> communities = communityRepository.findAll(pageable);
            return communities.map(communityMapper::toDTO);
        }
        Page<Community> communities = communityRepository.findByLocationContainingIgnoreCase(location, pageable);
        return communities.map(communityMapper::toDTO);
    }

    // Get a community by ID
    @Transactional(readOnly = true)
    public CommunityDTO getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));
        return communityMapper.toDTO(community);
    }

    // Update a community
    public CommunityDTO updateCommunity(Long id, CreateCommunityRequest request) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id: " + id));

        boolean isNameChanged = !community.getName().equals(request.getName());
        boolean isLocationChanged = !community.getLocation().equals(request.getLocation());

        if (isNameChanged || isLocationChanged) {
            if (communityRepository.existsByNameAndLocation(request.getName(), request.getLocation())) {
                throw new DuplicateEntityException("A community with the same name already exists in the this location.");
            }
        }
        community.setName(request.getName());
        community.setLocation(request.getLocation());
        community.setDescription(request.getDescription());
        Community updatedCommunity = communityRepository.save(community);
        return communityMapper.toDTO(updatedCommunity);
    }

    @Transactional(readOnly = true)
    public List<CommunityDTO> getCommunitiesByName(String name) {
        Pageable pageable = PageRequest.of(0, 10); // limit results
        Page<Community> communities = communityRepository.findByNameContainingIgnoreCase(name, pageable);
        return communities.getContent()
                .stream()
                .map(communityMapper::toDTO)
                .collect(Collectors.toList());
    }
}

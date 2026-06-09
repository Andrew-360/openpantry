package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.dto.community.CommunityDTO;
import com.communityPantry.communityPantry.dto.community.CreateCommunityRequest;
import com.communityPantry.communityPantry.service.CommunityService;
import com.communityPantry.communityPantry.web.interfaces.CommunityControllerInterface;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
public class CommunityController implements CommunityControllerInterface {
    private final CommunityService communityService;

    // Constructor for dependency injection
    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // Create a new community
    @Override
    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunity(Authentication authentication,
            @Valid @RequestBody CreateCommunityRequest request) {
        String username = authentication.getName();
        CommunityDTO createdCommunity = communityService.createCommunity(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommunity);
    }

    // Delete a community
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.noContent().build();
    }

    // Get all communities in a location
    @Override
    @GetMapping
    public ResponseEntity<Page<CommunityDTO>> getAllCommunitiesInLocation(
            @RequestParam(required = false) String location,
            Pageable pageable) {
        Page<CommunityDTO> communities = communityService.getAllCommunitiesInLocation(location, pageable);
        return ResponseEntity.ok(communities);
    }

    // Get a community by ID
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunityById(@PathVariable Long id) {
        CommunityDTO community = communityService.getCommunityById(id);
        return ResponseEntity.ok(community);
    }

    // Update a community
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<CommunityDTO> updateCommunity(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommunityRequest request) {
        CommunityDTO updatedCommunity = communityService.updateCommunity(id, request);
        return ResponseEntity.ok(updatedCommunity);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommunityDTO>> getCommunityByName(@RequestParam String name) {
        List<CommunityDTO> communities = communityService.getCommunitiesByName(name);
        return ResponseEntity.ok(communities);
    }
}

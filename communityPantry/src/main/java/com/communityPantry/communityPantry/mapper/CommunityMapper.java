package com.communityPantry.communityPantry.mapper;

import com.communityPantry.communityPantry.domain.Community;
import com.communityPantry.communityPantry.dto.community.CommunityDTO;
import com.communityPantry.communityPantry.dto.community.CreateCommunityRequest;
import org.springframework.stereotype.Component;

@Component
public class CommunityMapper {
    public Community toEntity(CreateCommunityRequest request) {
        Community community = new Community();
        community.setName(request.getName());
        community.setLocation(request.getLocation());
        community.setDescription(request.getDescription());
        return community;
    }

    public CommunityDTO toDTO(Community community) {
        CommunityDTO dto = new CommunityDTO();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setLocation(community.getLocation());
        dto.setDescription(community.getDescription());
        return dto;
    }
}

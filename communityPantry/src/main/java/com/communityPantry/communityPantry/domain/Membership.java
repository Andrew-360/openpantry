package com.communityPantry.communityPantry.domain;

import com.communityPantry.communityPantry.domain.enums.CommunityRole;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommunityRole role;

    @Column(nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    private Boolean isBanned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community")
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfile")
    private UserProfile userProfile;

    public Membership() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommunityRole getRole() {
        return role;
    }

    public void setRole(CommunityRole role) {
        this.role = role;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public static Membership create(
            CommunityRole role,
            LocalDate joinDate,
            Boolean isBanned,
            Community community,
            UserProfile userProfile) {
        Membership membership = new Membership();
        membership.setRole(role);
        membership.setJoinDate(joinDate);
        membership.setIsBanned(isBanned);
        membership.setCommunity(community);
        membership.setUserProfile(userProfile);
        return membership;
    }

    @Override
    public String toString() {
        Long communityId = community != null ? community.getId() : null;
        Long userProfileId = userProfile != null ? userProfile.getId() : null;
        return "Membership{id=" + id
                + ", role=" + role
                + ", joinDate=" + joinDate
                + ", isBanned=" + isBanned
                + ", communityId=" + communityId
                + ", userProfileId=" + userProfileId
                + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Membership that))
            return false;
        return id != null && that.getId() != null && Objects.equals(id, that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

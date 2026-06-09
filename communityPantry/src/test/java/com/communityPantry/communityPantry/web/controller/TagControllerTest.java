package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.security.AuthEntryPointJwt;
import com.communityPantry.communityPantry.security.JwtService;
import com.communityPantry.communityPantry.service.TagService;
import com.communityPantry.communityPantry.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.doThrow;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @Test
    void createTag() throws Exception {

        Tag tag = new Tag("vegan");
        tag.setId(1L);

        when(tagService.createTag("vegan")).thenReturn(tag);

        mockMvc.perform(post("/tags")
                        .with(csrf())
                        .with(user("test-user"))
                        .param("name", "vegan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("vegan"));
    }

    @Test
    void getAllTags() throws Exception {
        Tag tag1 = new Tag("vegan");
        tag1.setId(1L);

        Tag tag2 = new Tag("gluten-free");
        tag2.setId(2L);

        when(tagService.getAllTags()).thenReturn(List.of(tag1, tag2));

        mockMvc.perform(get("/tags")
                        .with(user("test-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTagById() throws Exception {

        Tag tag = new Tag("vegan");
        tag.setId(1L);

        when(tagService.getTagById(1L)).thenReturn(tag);

        mockMvc.perform(get("/tags/1")
                        .with(user("test-user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("vegan"));
    }

    @Test
    void deleteTag() throws Exception {

        doNothing().when(tagService).deleteTag(1L);

        mockMvc.perform(delete("/tags/1")
                        .with(csrf())
                        .with(user("test-user")))
                .andExpect(status().isNoContent());
    }

    @Test
    void getTagById_notFound() throws Exception {

        when(tagService.getTagById(99L))
                .thenThrow(new EntityNotFoundException("Tag not found: 99"));

        mockMvc.perform(get("/tags/99")
                        .with(user("test-user")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTag_notFound() throws Exception {

        doThrow(new EntityNotFoundException("Tag not found: 99"))
                .when(tagService).deleteTag(99L);

        mockMvc.perform(delete("/tags/99")
                        .with(csrf())
                        .with(user("test-user")))
                .andExpect(status().isNotFound());
    }
}
package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagControllerUnitTest {

    @Mock
    private TagService tagService;

    @InjectMocks
    private TagController tagController;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag("vegan");
        tag.setId(1L);
    }

    // createTag
    @Test
    void createTag() {
        when(tagService.createTag("vegan")).thenReturn(tag);

        Tag result = tagController.createTag("vegan");

        assertNotNull(result);
        assertEquals("vegan", result.getName());
        verify(tagService, times(1)).createTag("vegan");
    }

    // getTags
    @Test
    void getTags() {
        Tag tag2 = new Tag("gluten-free");
        tag2.setId(2L);

        when(tagService.getAllTags()).thenReturn(List.of(tag, tag2));

        List<Tag> result = tagController.getTags();

        assertEquals(2, result.size());
        verify(tagService, times(1)).getAllTags();
    }

    @Test
    void getTagsEmpty() {
        when(tagService.getAllTags()).thenReturn(List.of());

        List<Tag> result = tagController.getTags();

        assertTrue(result.isEmpty());
    }

    // getTag
    @Test
    void getTag() {
        when(tagService.getTagById(1L)).thenReturn(tag);

        Tag result = tagController.getTag(1L);

        assertEquals(1, result.getId());
        assertEquals("vegan", result.getName());
        verify(tagService, times(1)).getTagById(1L);
    }

    @Test
    void getTagInvalid() {
        when(tagService.getTagById(999L)).thenThrow(new NoSuchElementException());

        assertThrows(NoSuchElementException.class, () -> tagController.getTag(999L));
    }

    // deleteTag
    @Test
    void deleteTag() {
        doNothing().when(tagService).deleteTag(1L);

        tagController.deleteTag(1L);

        verify(tagService, times(1)).deleteTag(1L);
    }
}
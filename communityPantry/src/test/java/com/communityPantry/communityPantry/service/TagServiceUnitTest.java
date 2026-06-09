package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceUnitTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag("vegan");
        tag.setId(1L);
    }

    // createTag
    @Test
    void createTag() {
        when(tagRepository.findByName("vegan")).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.createTag("vegan");

        assertNotNull(result);
        assertEquals("vegan", result.getName());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void createTagTrim() {
        Tag lowercaseTag = new Tag("vegan");
        lowercaseTag.setId(1L);

        when(tagRepository.findByName("vegan")).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenReturn(lowercaseTag);

        Tag result = tagService.createTag("  VEGAN  ");

        assertEquals("vegan", result.getName());
        verify(tagRepository).findByName("vegan");
    }

    @Test
    void duplicateTag() {
        when(tagRepository.findByName("vegan")).thenReturn(tag);

        Tag result = tagService.createTag("vegan");

        assertEquals(tag.getId(), result.getId());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    // getAllTags
    @Test
    void getAllTags() {
        Tag tag2 = new Tag("gluten-free");
        tag2.setId(2L);

        when(tagRepository.findAll()).thenReturn(List.of(tag, tag2));

        List<Tag> result = tagService.getAllTags();

        assertEquals(2, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void getAllTagsNone() {
        when(tagRepository.findAll()).thenReturn(List.of());

        List<Tag> result = tagService.getAllTags();

        assertTrue(result.isEmpty());
    }

    // getTagById
    @Test
    void getTagById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Tag result = tagService.getTagById(1L);

        assertEquals(1, result.getId());
        assertEquals("vegan", result.getName());
    }

    @Test
    void getTagByIdInvalid() {
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(999L));
    }

    @Test
    void deleteTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        doNothing().when(tagRepository).delete(tag);

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).delete(tag);
    }
}
//Realised going over this that there is no point to this entire test file

package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TagRepositoryUnitTest {

    @Mock
    private TagRepository tagRepository;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag("vegan");
        tag.setId(1L);
    }

    @Test
    void save() {
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagRepository.save(tag);

        assertNotNull(result);
        assertEquals("vegan", result.getName());
        verify(tagRepository, times(1)).save(tag);
    }

    @Test
    void findById() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        Optional<Tag> result = tagRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
    }

    @Test
    void findByIdInvalid() {
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Tag> result = tagRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByName() {
        when(tagRepository.findByName("vegan")).thenReturn(tag);

        Tag result = tagRepository.findByName("vegan");

        assertNotNull(result);
        assertEquals("vegan", result.getName());
    }

    @Test
    void findByNameInvalid() {
        when(tagRepository.findByName("unknown")).thenReturn(null);

        Tag result = tagRepository.findByName("unknown");

        assertNull(result);
    }

    @Test
    void findAll() {
        Tag tag2 = new Tag("gluten-free");
        tag2.setId(2L);

        when(tagRepository.findAll()).thenReturn(List.of(tag, tag2));

        Iterable<Tag> result = tagRepository.findAll();
        List<Tag> tags = (List<Tag>) result;

        assertEquals(2, tags.size());
    }

    @Test
    void deleteById() {
        doNothing().when(tagRepository).deleteById(1L);

        tagRepository.deleteById(1L);

        verify(tagRepository, times(1)).deleteById(1L);
    }
}
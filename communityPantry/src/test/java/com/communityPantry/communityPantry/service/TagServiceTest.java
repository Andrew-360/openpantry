package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb", "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "jwt.secret=V7FFekAEKlD33bhY2zfoCsLOPcreeQHR53eahcdKd7c"})
@Transactional
public class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    void createTag() {

        Tag tag = tagService.createTag("tag1");

        assertNotNull(tag);
        assertEquals("tag1", tag.getName());
    }

    @Test
    void noDuplicates() {

        Tag tag1 = tagService.createTag("tag1");
        Tag tag2 = tagService.createTag("tag1");

        assertEquals(tag1.getId(), tag2.getId());
    }

    @Test
    void getAllTags() {

        Tag tag1 = tagService.createTag("tag1");
        Tag tag2 = tagService.createTag("tag2");

        List<Tag> tags = tagService.getAllTags();

        assertEquals(2, tags.size());
    }

    @Test
    void getTagById() {

        Tag tag = tagService.createTag("tag1");
        Tag tag2 = tagService.getTagById(tag.getId());

        assertEquals(tag.getId(), tag2.getId());
    }

    @Test
    void deleteTag() {

        Tag tag = tagService.createTag("tag1");
        tagService.deleteTag(tag.getId());

        assertThrows(RuntimeException.class, () -> tagService.getTagById(tag.getId()));
    }

    @Test
    void getTagByIdNotFound() {

        assertThrows(EntityNotFoundException.class, () -> tagService.getTagById(999L));
    }

    @Test
    void deleteTagRemovesList() {

        Tag tag = tagService.createTag("tag1");
        tagService.deleteTag(tag.getId());

        List<Tag> tags = tagService.getAllTags();
        assertEquals(0, tags.size());
    }




}

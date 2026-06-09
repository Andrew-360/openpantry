package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.Tag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (properties = {
            "spring.datasource.url=jdbc:h2:mem:testdb", "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "jwt.secret=V7FFekAEKlD33bhY2zfoCsLOPcreeQHR53eahcdKd7c"})
@Transactional
public class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    void findByNameReturnsTag() {

        Tag tag = new Tag("vegan");
        tagRepository.save(tag);

        Tag found = tagRepository.findByName("vegan");

        assertNotNull(found);
        assertEquals("vegan", found.getName());
    }

    @Test
    void findByNameReturnsNullIfNotFound() {

        Tag found = tagRepository.findByName("nonexistent");

        assertNull(found);
    }
}
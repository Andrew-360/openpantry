package com.communityPantry.communityPantry.service;

import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.exception.EntityNotFoundException;
import com.communityPantry.communityPantry.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createTag(String name) {

        name = name.trim().toLowerCase();

        Tag existingTag = tagRepository.findByName(name);

        if (existingTag != null) {
            return existingTag;
        }

        Tag tag = new Tag(name);
        return tagRepository.save(tag);
    }

    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(("Tag not found ") + id));


        tag.getFoodItems().forEach(item -> item.getTags().remove(tag));
        tagRepository.delete(tag);
    }

    public List<Tag> getAllTags() {
        return (List<Tag>) tagRepository.findAll();
    }

    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + id));
    }
}

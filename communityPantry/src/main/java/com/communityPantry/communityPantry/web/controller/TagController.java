package com.communityPantry.communityPantry.web.controller;

import com.communityPantry.communityPantry.domain.Tag;
import com.communityPantry.communityPantry.service.TagService;
import com.communityPantry.communityPantry.web.interfaces.TagControllerInterface;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController implements TagControllerInterface {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    @PostMapping
    public Tag createTag(@RequestParam String name) {
        return tagService.createTag(name);
    }

    @Override
    @GetMapping
    public List<Tag> getTags() {
        return tagService.getAllTags();
    }

    @Override
    @GetMapping("/{id}")
    public Tag getTag(@PathVariable Long id) {
        return tagService.getTagById(id);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

}

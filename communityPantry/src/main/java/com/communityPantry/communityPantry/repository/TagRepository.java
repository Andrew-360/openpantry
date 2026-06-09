package com.communityPantry.communityPantry.repository;

import com.communityPantry.communityPantry.domain.Tag;
import org.springframework.data.repository.CrudRepository;
import java.util.Set;
import java.util.Optional;

public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByName(String name);

    Set<Tag> findByIdIn(Set<Long> ids);

    Optional<Tag> findById(Long id);
}

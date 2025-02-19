package com.davidp.chessjourney.domain.common;


import java.util.List;
import java.util.UUID;

public interface TagRepository {

    List<Tag> getAll();
    Tag getTagById(UUID id);
    UUID save(Tag tag);

}

